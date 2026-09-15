package com.example.api.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Render (وبعض المنصات السحابية) بيدّي رابط قاعدة البيانات بصيغة:
 *   postgres://user:password@host:port/dbname
 *
 * لكن Spring Boot محتاج صيغة JDBC:
 *   jdbc:postgresql://host:port/dbname
 *
 * الكلاس ده بيحوّل الصيغة تلقائيًا وقت التشغيل، فمش محتاج تعدّل أي حاجة يدويًا.
 * لو الرابط أصلاً بصيغة jdbc: بيسيبه زي ما هو.
 */
public class DatabaseUrlPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty("DATABASE_URL");

        // لو مفيش رابط، أو الرابط أصلاً بصيغة JDBC، مفيش داعي لأي تحويل
        if (databaseUrl == null || databaseUrl.startsWith("jdbc:")) {
            return;
        }

        if (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://")) {
            try {
                URI uri = new URI(databaseUrl);

                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String database = uri.getPath(); // بيبدأ بـ "/"

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + database;

                Map<String, Object> properties = new HashMap<>();
                properties.put("spring.datasource.url", jdbcUrl);

                // لو اسم المستخدم والباسورد موجودين جوه الرابط نفسه، نستخرجهم
                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    properties.put("spring.datasource.username", parts[0]);
                    properties.put("spring.datasource.password", parts[1]);
                }

                environment.getPropertySources()
                        .addFirst(new MapPropertySource("renderDatabaseUrl", properties));

            } catch (Exception e) {
                throw new IllegalStateException("تعذّر قراءة قيمة DATABASE_URL: " + e.getMessage(), e);
            }
        }
    }
}
