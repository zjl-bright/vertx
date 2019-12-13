package me.zjl.boot.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * TODO
 *
 * @Auther: zjl
 * @Date: 2019-11-07
 * @Version: 1.0
 */
public class PropertiesModule implements Module {

    Logger log = LoggerFactory.getLogger(PropertiesModule.class);

    private final String arg;

    public PropertiesModule(String[] args){
        if(Objects.isNull(args) || args.length < 1 || Objects.equals("", args[0])){
            this.arg = "application.properties";
        }else{
            this.arg = args[0];
        }
    }

    @Override
    public void configure(Binder binder){
        try{
            loadProperties(binder);
        } catch (Exception e) {
            log.error("load properties failed, by {}", arg);
        }
    }

    private void loadProperties(Binder binder) throws Exception{
        InputStream input = null;
        Properties prop = new Properties();
        try{
            if(arg.endsWith(".properties")){
                File configFile = new File(arg);
                if(!configFile.exists()){
                    input = ClassLoader.getSystemResourceAsStream(arg);
                    prop.load(new InputStreamReader(input, StandardCharsets.UTF_8));
                }else{
                    input = new FileInputStream(configFile);
                    prop.load(new InputStreamReader(input, StandardCharsets.UTF_8));
                }
            }else{
                input = ClassLoader.getSystemResourceAsStream("application-" + arg +".properties");
                prop.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            }
            Names.bindProperties(binder, prop);
        } catch (Exception e){
            throw e;
        } finally {
            if(input != null){
                input.close();
            }
        }
    }
}
