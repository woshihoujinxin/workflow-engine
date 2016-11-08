package com.keeper.service;

import org.databene.feed4junit.ChildRunner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;

/*
 * (c) Copyright 2011-2012 by Volker Bergmann. All rights reserved.
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


/**
 * 重写的MyChildRunner
 * Created by houjinxin on 16/4/12.
 */
public class MyChildRunner extends ChildRunner {

    private MyFeeder feeder;
    private FrameworkMethod method;
    private RunNotifier notifier;

    public MyChildRunner(MyFeeder feeder, FrameworkMethod method, RunNotifier notifier) {
        super(null, null, null);
        this.feeder = feeder;
        this.method = method;
        this.notifier = notifier;
    }

    public FrameworkMethod getMethod() {
        return method;
    }

    public String getPath() {
        return method.getMethod().getDeclaringClass().getName() + '.' + method.getMethod().getName() + '#';
    }

    public String getPathType() {
        return "INVOCATION";
    }

    public void run() {
        feeder.runChild(method, notifier);
    }

    @Override
    public String toString() {
        return method.toString();
    }

}