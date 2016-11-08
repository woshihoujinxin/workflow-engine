###概述
1、这是一个简单的工作流引擎，扩展性强，适用于按照一定流程执行后解决一个具体问题的场景，如通过发送多个http请求最终获取一个想要的结果
2、项目主要由以下几个部分构成：
#### workflow-engine-core
    核心模块，工作流引擎的运转机制
#### workflow-engine-demo
    demo程序，可以从人保和平安抓取车辆的报价数据，有车人士可以试试报价功能，详见workflow-engine-demo/readme.md
#### workflow-engine-expression
    表达式解析引擎，用于解析参数的规则配置