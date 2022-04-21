//package pt.iscteiul.datainjector;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.env.Environment;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//import java.util.HashMap;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        entityManagerFactoryRef = "entityManagerFactory",
//        basePackages = { "pt.iscteiul.datainjector.local" })
//public class localDBConfig {
//
//    @Autowired
//    private Environment env;
//
//    @Primary
//    @Bean(name = "localDatasourceProperties")
//    @ConfigurationProperties(prefix = "spring.local.datasource")
//    public DataSourceProperties dataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Primary
//    @Bean(name = "localDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource localDataSource(@Qualifier("localDatasourceProperties") DataSourceProperties properties) {
//        return properties.initializeDataSourceBuilder().build();
//    }
//
//    @Primary
//    @Bean(name = "entityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean
//    entityManagerFactory(
//            EntityManagerFactoryBuilder builder,
//            @Qualifier("localDataSource") DataSource dataSource
//    ) {
//        return builder
//                .dataSource(dataSource)
//                .packages("pt.iscteiul.datainjector.local")
//                .persistenceUnit("local")
//                .build();
//    }
//
//    @Primary
//    @Bean(name = "transactionManager")
//    @ConfigurationProperties("spring.jpa")
//    public PlatformTransactionManager transactionManager(
//            @Qualifier("entityManagerFactory") EntityManagerFactory
//                    entityManagerFactory
//    ) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
//}
