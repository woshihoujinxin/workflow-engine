package com.keeper.service;

/*
 * (c) Copyright 2010-2012 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import org.databene.benerator.Generator;
import org.databene.benerator.anno.AnnotationMapper;
import org.databene.benerator.anno.ThreadPoolSize;
import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Period;
import org.databene.commons.converter.AnyConverter;
import org.databene.feed4junit.ChildRunner;
import org.databene.feed4junit.ErrorReportingFrameworkMethod;
import org.databene.feed4junit.Feed4JUnitConfig;
import org.databene.feed4junit.FrameworkMethodWithParameters;
import org.databene.feed4junit.Scheduler;
import org.databene.feed4junit.TestInfoProvider;
import org.databene.feed4junit.scheduler.DefaultFeedScheduler;
import org.databene.platform.java.Entity2JavaConverter;
import org.databene.script.DatabeneScriptParser;
import org.databene.script.Expression;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 使用Feeder4Junit作为数据提供者，因其与Spring不能同时使用，所以重写了Feeder.
 * 使其继承Spring的SpringJUnit4ClassRunner，保留了Spring的全部功能
 * Created by houjinxin on 16/4/12.
 */
public class MyFeeder extends SpringJUnit4ClassRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyFeeder.class);

    public static final long DEFAULT_TIMEOUT = Period.WEEK.getMillis();

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    private Feed4JUnitConfig config;
    private BeneratorContext context;
    private AnnotationMapper annotationMapper;

    private List<FrameworkMethod> children;
    private RunnerScheduler scheduler;

    public MyFeeder(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected String testName(FrameworkMethod method) {
        return (method instanceof FrameworkMethodWithParameters ? method.toString() : super.testName(method));
    }

    @Override
    public void setScheduler(RunnerScheduler scheduler) {
        this.scheduler = scheduler;
        super.setScheduler(scheduler);
    }

    /**
     * Instantiates a test class and initializes attributes
     * which have been marked with a @Source annotation.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected Object createTest() throws Exception {
        Object testObject = super.createTest();
        for (FrameworkField attribute : getTestClass().getAnnotatedFields(org.databene.benerator.anno.Source.class)) {
            if ((attribute.getField().getModifiers() & Modifier.PUBLIC) == 0)
                throw new ConfigurationError("Attribute '" + attribute.getField().getName() + "' must be public");
            Generator<?> generator = getAnnotationMapper().createAndInitAttributeGenerator(attribute.getField(), getContext());
            if (generator != null) {
                ProductWrapper wrapper = new ProductWrapper();
                wrapper = generator.generate(wrapper);
                if (wrapper != null)
                    attribute.getField().set(testObject, wrapper.unwrap());
            }
        }
        return testObject;
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        if (children == null) {
            children = new ArrayList<FrameworkMethod>();
            TestClass testClass = getTestClass();
            AnnotationMapper annotationMapper = getAnnotationMapper();
            for (FrameworkMethod method : testClass.getAnnotatedMethods(Test.class)) {
                if (method.getMethod().getParameterTypes().length == 0) {
                    // standard JUnit test method
                    children.add(method);
                    continue;
                } else {
                    // parameterized Feed4JUnit test method
                    BeneratorContext context = getContext();
                    context.setGeneratorFactory(config.createDefaultGeneratorFactory());
                    annotationMapper.parseClassAnnotations(testClass.getAnnotations(), context);
                    List<? extends FrameworkMethod> parameterizedTestMethods;
                    parameterizedTestMethods = computeParameterizedTestMethods(method.getMethod(), context);
                    children.addAll(parameterizedTestMethods);
                }
            }
        }
        return children;
    }

    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        validatePublicVoidMethods(Test.class, false, errors);
    }

    // test execution --------------------------------------------------------------------------------------------------

    @Override
    protected Statement childrenInvoker(final RunNotifier notifier) {
        return new Statement() {
            @Override
            public void evaluate() {
                runChildren(notifier);
            }
        };
    }

    private void runChildren(final RunNotifier notifier) {
        RunnerScheduler scheduler = getScheduler();
        for (FrameworkMethod method : getChildren())
            scheduler.schedule(new MyChildRunner(this, method, notifier));
        scheduler.finished();
    }

    /** this is needed to make the runChild() method public and thus accessible from other classes, especially {@link ChildRunner}. */
    @Override
    public void runChild(FrameworkMethod method, RunNotifier notifier) {
        super.runChild(method, notifier);
    }

    public RunnerScheduler getScheduler() {
        if (scheduler == null)
            scheduler = createDefaultScheduler();
        return scheduler;
    }

    protected RunnerScheduler createDefaultScheduler() {
        TestClass testClass = getTestClass();
        Scheduler annotation = testClass.getJavaClass().getAnnotation(Scheduler.class);
        if (annotation != null) {
            String spec = annotation.value();
            Expression<?> bean = DatabeneScriptParser.parseBeanSpec(spec);
            return (RunnerScheduler) bean.evaluate(null);
        } else {
            return new DefaultFeedScheduler(1, DEFAULT_TIMEOUT);
        }
    }



    // helpers ---------------------------------------------------------------------------------------------------------

    private void validatePublicVoidMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Throwable> errors) {
        List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(annotation);
        for (FrameworkMethod eachTestMethod : methods)
            eachTestMethod.validatePublicVoid(isStatic, errors);
    }

    private List<FrameworkMethodWithParameters> computeParameterizedTestMethods(Method method, BeneratorContext context) {
        Integer threads = getThreadCount(method);
        long timeout = getTimeout(method);
        List<FrameworkMethodWithParameters> result = new ArrayList<FrameworkMethodWithParameters>();
        Class<?>[] parameterTypes = method.getParameterTypes();
        AnnotationMapper annotationMapper = getAnnotationMapper();
        TestInfoProvider infoProvider = getConfig().getInfoProvider();
        try {
            Generator<Object[]> paramGenerator = annotationMapper.createAndInitMethodParamsGenerator(method, context);
            Class<?>[] expectedTypes = parameterTypes;
            ProductWrapper<Object[]> wrapper = new ProductWrapper<Object[]>();
            int count = 0;
            while ((wrapper = paramGenerator.generate(wrapper)) != null) {
                Object[] generatedParams = wrapper.unwrap();
                if (generatedParams.length > expectedTypes.length) // imported data may have more columns than the method parameters, ...
                    generatedParams = Arrays.copyOfRange(generatedParams, 0, expectedTypes.length); // ...so cut them
                for (int i = 0; i < generatedParams.length; i++) {
                    generatedParams[i] = Entity2JavaConverter.convertAny(generatedParams[i]);
                    generatedParams[i] = AnyConverter.convert(generatedParams[i], parameterTypes[i]);
                }
                // generated params may be to few, e.g. if an XLS row was imported with trailing nulls,
                // so create an array of appropriate size
                Object[] usedParams = new Object[parameterTypes.length];
                System.arraycopy(generatedParams, 0, usedParams, 0, Math.min(generatedParams.length, usedParams.length));
                String info = infoProvider.testInfo(method, usedParams);
                result.add(new FrameworkMethodWithParameters(method, usedParams, threads, timeout, info));
                count++;
            }
            if (count == 0)
                throw new RuntimeException("No parameter values available for method: " + method);
        } catch (Exception e) {
            LOGGER.error("Error creating test parameters", e);
            String info = infoProvider.errorInfo(method, e);
            result.add(new ErrorReportingFrameworkMethod(method, e, info));
        }
        return result;
    }

    private static Integer getThreadCount(Method method) {
        ThreadPoolSize methodAnnotation = method.getAnnotation(ThreadPoolSize.class);
        if (methodAnnotation != null)
            return methodAnnotation.value();
        Class<?> testClass = method.getDeclaringClass();
        ThreadPoolSize classAnnotation = testClass.getAnnotation(ThreadPoolSize.class);
        if (classAnnotation != null)
            return classAnnotation.value();
        return null;
    }

    private static long getTimeout(Method method) {
        return DEFAULT_TIMEOUT;
    }

    private Feed4JUnitConfig getConfig() {
        if (this.config == null)
            init();
        return this.config;
    }

    public BeneratorContext getContext() {
        if (this.config == null)
            init();
        return this.context;
    }

    private AnnotationMapper getAnnotationMapper() {
        if (this.config == null)
            init();
        return this.annotationMapper;
    }

    private void init() {
        this.config = new Feed4JUnitConfig();
        this.context = new DefaultBeneratorContext();
        this.annotationMapper = new AnnotationMapper(context.getDataModel(), config.getPathResolver());
    }

}
