package pt.iscteiul.datainjector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "cloudEntityManagerFactory",
        transactionManagerRef = "cloudTransactionManager",
        basePackages = "pt.iscteiul.datainjector.cloud.repository"
)
public class CloudMySQLConfig {
    @Autowired
    private Environment env;

    @Bean(name = "cloudDataSource")
    public DataSource cloudDatasource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(env.getProperty("spring.cloud.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.cloud.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.cloud.datasource.password"));
        dataSource.setDriverClassName(env.getProperty("spring.cloud.datasource.driver-class-name"));

        return dataSource;
    }

    @Bean(name = "cloudEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(cloudDatasource());
        em.setPackagesToScan("pt.iscteiul.datainjector.cloud.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("mysql.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.show-sql", env.getProperty("mysql.jpa.show-sql"));
        properties.put("hibernate.dialect", env.getProperty("mysql.jpa.database-platform"));

        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "cloudTransactionManager")
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }
}
