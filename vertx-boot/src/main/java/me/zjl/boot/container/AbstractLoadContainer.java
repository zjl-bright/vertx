package me.zjl.boot.container;

import com.google.inject.Injector;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import me.zjl.boot.annotation.NoProtected;
import me.zjl.boot.annotation.RequestMapping;
import me.zjl.boot.annotation.WorkerMapping;
import me.zjl.boot.route.DelegateRoute;
import me.zjl.boot.route.DelegateWork;
import me.zjl.boot.verticle.WebVerticle;
import me.zjl.boot.verticle.WorkerVerticle;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * created by zjl on 2019/2/26
 */

public abstract class AbstractLoadContainer implements Container {

    private final static Logger log = LoggerFactory.getLogger(AbstractLoadContainer.class);

    private final Set<Class<? extends Annotation>> annotatedClasses = new LinkedHashSet<>();

    private final List<DelegateRoute> routes = new ArrayList();

    private final List<DelegateWork> works = new ArrayList();

    private Reflections reflections;

    protected final List<DelegateRoute> routes() {
        return Collections.unmodifiableList(routes);
    }

    protected final List<DelegateWork> works() {
        return Collections.unmodifiableList(works);
    }

    @Override
    public void register(Class<? extends Annotation>... annotatedClasses){
        this.annotatedClasses.addAll(Arrays.asList(annotatedClasses));
    }

    @Override
    public void scan(Class<?> primarySource){
        reflections = new Reflections(
                new ConfigurationBuilder()
                        .addUrls(ClasspathHelper.forClass(primarySource))
                        .setScanners(
                                new TypeAnnotationsScanner(),
                                new SubTypesScanner(false),
                                new MethodAnnotationsScanner())
                        .filterInputsBy(
                                new FilterBuilder().includePackage(primarySource.getPackage().getName()))
        );
    }

    public void load(Injector injector){
        annotatedClasses.forEach(annotated -> {
            anaylesAnnotated(injector, reflections, annotated);
        });
    }

    private void anaylesAnnotated(Injector injector, Reflections reflections, final Class<? extends Annotation> annClass){

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(annClass);
        if(classes == null || classes.isEmpty()){
            log.warn("No @{} annotation on class", annClass.getName());
            return;
        }
        for(Class c : classes){
            Annotation classAnnotation = c.getAnnotation(annClass);
            String basePath = getRealPath(classAnnotation);

            Set<Method> methods = getMethodsAnnotatedWith(c, annClass);
            for(Method method : methods){
                Annotation methodAnnotation = method.getAnnotation(annClass);
                String methodPath = getRealPath(methodAnnotation);
                String path = basePath + methodPath;
                Object o = injector.getInstance(c);

                if(methodAnnotation instanceof RequestMapping){
                    RequestMapping requestMapping = (RequestMapping)methodAnnotation;
                    final String methodConsumes = requestMapping.consumes();
                    final String methodProduces = requestMapping.produces();
                    final HttpMethod httpMethod = requestMapping.method();

                    Annotation npClassAnnotation = c.getAnnotation(NoProtected.class);
                    Annotation npMethodAnnotation = method.getAnnotation(NoProtected.class);
                    DelegateRoute route;
                    if(Objects.nonNull(npClassAnnotation)){
                        route = new DelegateRoute(path, httpMethod, methodConsumes, methodProduces, method, o, true);
                    }else{
                        if(Objects.nonNull(npMethodAnnotation)){
                            route = new DelegateRoute(path, httpMethod, methodConsumes, methodProduces, method, o, true);
                        }else{
                            route = new DelegateRoute(path, httpMethod, methodConsumes, methodProduces, method, o, false);
                        }
                    }

                    if(routes.contains(route)){
                        throw new RuntimeException("the same route , path : " + route.getPath() + " ,method : " + route.getMethod());
                    }
                    routes.add(route);
                } else if(methodAnnotation instanceof WorkerMapping){
                    WorkerMapping workerMapping = (WorkerMapping)methodAnnotation;
                    String type = workerMapping.type();
                    path = path.substring(1);
                    DelegateWork work = new DelegateWork(path, method, o);
                    if(works.contains(work)){
                        throw new RuntimeException("the same work , path : " + work.getPath());
                    }
                    works.add(work);
                }
            }
        }
    }

    private String getRealPath(Annotation classAnnotation){
        String path = "";
        if(classAnnotation instanceof RequestMapping){
            RequestMapping requestMapping = (RequestMapping)classAnnotation;
            path = getRoutePath(requestMapping.value());
        }else if(classAnnotation instanceof WorkerMapping){
            WorkerMapping workerMapping = (WorkerMapping)classAnnotation;
            path = getBusPath(workerMapping.value());
        }
        return path;
    }

    private String getRoutePath(String path){
        if(!"".equals(path)){
            if(!path.startsWith("/")){
                path = "/" + path;
            }
            if(path.endsWith("/")){
                path = path.substring(0, path.lastIndexOf("/"));
            }
        }
        return path;
    }

    private String getBusPath(String path){
        if(!"".equals(path)){
            if(!path.startsWith(".")){
                path = "." + path;
            }
            if(path.endsWith(".")){
                path = path.substring(0, path.lastIndexOf("."));
            }
        }
        return path;
    }

    private static Set<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
        final List<String> methodNames = new ArrayList<>();
        final Set<Method> methods = new HashSet<Method>();
        Class<?> klass = type;
        while (klass != Object.class) {
            final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
            for (final Method method : allMethods) {
                if (method.isAnnotationPresent(annotation)) {
                    if (!methodNames.contains(method.getName())) {
                        methods.add(method);
                        methodNames.add(method.getName());
                    }
                }
            }
            klass = klass.getSuperclass();
        }
        return methods;
    }

    protected synchronized void deployVerticle(Vertx vertx){

        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", "application.json"));
        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);
        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        retriever.getConfig(res -> {
            if (res.succeeded()) {
                JsonObject config = res.result();
                vertx.deployVerticle(WebVerticle.class.getName(), new DeploymentOptions().setConfig(config), web -> {
                    if (web.succeeded()) {
                        log.info(" WebVerticle 启动完毕！");
                    } else {
                        log.error("WebVerticle deployment failed, cause by : ", web.cause());
                    }
                });
            } else {
                log.error("读取application.json失败：", res.cause());
            }
        });

        vertx.deployVerticle(WorkerVerticle.class.getName(), new DeploymentOptions()
                .setWorker(true));
    }
}
