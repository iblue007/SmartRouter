package com.dpzx.route_compiler;


import com.google.auto.service.AutoService;

import java.util.Map;
import java.util.Set;
import com.dpzx.route_compiler.anotation.Route;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 在这个类上添加了@AutoService注解，它的作用是用来生成
 * META-INF/services/javax.annotation.processing.Processor文件的，
 * 也就是我们在使用注解处理器的时候需要手动添加
 * META-INF/services/javax.annotation.processing.Processor，
 * 而有了@AutoService后它会自动帮我们生成。
 * AutoService是Google开发的一个库，使用时需要在build中添加依赖
 * Create by xuqunxing on  2020/10/14
 */
@AutoService(Processor.class)
/**
 * 注册给哪些注解的  替代 {@link AbstractProcessor#getSupportedAnnotationTypes()} 函数
 * 声明我们要处理哪一些注解 该方法返回字符串的集合表示该处理器用于处理哪些注解
 */
@SupportedAnnotationTypes({consts.ANN_TYPE_ROUTE})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RouteProcessor extends AbstractProcessor {
    //获得apt的日志输出
    private Log log;
    //节点工具类（类，函数，属性等都是节点）
    private Elements elementUtils;
    //类信息-工具类
    private Types typeUtils;
    // 类/资源生成器
    private Filer filer;
    /**
     * 当前App的包名
     */
    private String moduleName;

    /**
     * 编译期间，init()会自动被注解处理工具调用，并传入ProcessingEnviroment参数，通过该参数可以获取到很多有用的工具类: Elements , Types , Filer **等等
     *
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //获得apt的日志输出
        log = Log.newLog(processingEnvironment.getMessager());
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filer = processingEnvironment.getFiler();
        // 获取当前Application的包名
        Map<String, String> options = processingEnv.getOptions();
        if (options != null) {
            moduleName = options.get(consts.KEY_MODULE_NAME);
        }
        log.i("======init");
    }

    /**
     * Annotation Processor扫描出的结果会存储进roundEnv中，可以在这里获取到注解内容，编写你的操作逻辑。注意,process()函数中不能直接进行异常抛出,否则的话,运行Annotation Processor的进程会异常崩溃,然后弹出一大堆让人捉摸不清的堆栈调用日志显示
     *
     * @param set
     * @param roundEnvironment 表示当前或是之前的运行环境,可以通过该对象查找找到的注解。
     * @return true 表示后续处理器不会再处理(已经处理)
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(Route.class);
        if (!(elementsAnnotatedWith == null || elementsAnnotatedWith.isEmpty())) {
            TypeMirror type_Activity = elementUtils.getTypeElement(consts.ACTIVITY).asType();
             /*
              参数类型Map<String,RouteMeta>
             */
            log.i("======process");
            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouteMeta.class));
            ParameterSpec groupParamSpec = ParameterSpec.builder(parameterizedTypeName, "routes").build();
             /*
              methodBuilder 方法名
              addAnnotation 方法添加注解
              addModifiers  方法访问限制类型
              addParameter  添加参数
              拼装出loadInto(Map<String, RouteMeta> routes) 这个方法和参数
             */
            MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(consts.METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(groupParamSpec);

            ClassName routeMetaClassName = ClassName.get(RouteMeta.class);
            ClassName routeTypeClassName = ClassName.get(RouteType.class);
            //遍历@Route注解的所有Activity
            for (Element element : elementsAnnotatedWith) {
                TypeMirror typeMirror = element.asType();
                Route annotation = element.getAnnotation(Route.class);
                RouteMeta routeMeta = null;
                if (typeUtils.isSubtype(typeMirror, type_Activity)) {
                    routeMeta = new RouteMeta(annotation.path(), RouteType.ACTIVITY);
                }
                //获取被注解的类的类名
                ClassName className = ClassName.get((TypeElement) element);
                /*
                  方法内的添加路由语句
                  routes.put(routeMeta.getPath(),RouteMeta.build(routeMeta.getPath(),RouteType.ACTIVITY,className.class))
                 */
                loadIntoMethodOfGroupBuilder.addStatement(
                        "routes.put($S,$T.build($S,$T." + RouteType.ACTIVITY + ", $T.class))",
                        routeMeta.getPath(),
                        routeMetaClassName,
                        routeMeta.getPath(),
                        routeTypeClassName,
                        className);

            }
            /*
              构建java文件
             */
            try {
                JavaFile.builder(consts.PACKAGE_OF_GENERATE_FILE,
                        TypeSpec.classBuilder(consts.NAME_OF_ROUTE + moduleName)
                                .addJavadoc(consts.WARNING_TIPS)
                                .addSuperinterface(ClassName.get(elementUtils.getTypeElement(consts.IROUTE)))
                                .addModifiers(Modifier.PUBLIC)
                                .addMethod(loadIntoMethodOfGroupBuilder.build())
                                .build()
                ).build().writeTo(filer);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}