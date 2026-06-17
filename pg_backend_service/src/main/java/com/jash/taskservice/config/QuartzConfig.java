@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJobStoreType(SchedulerFactoryBean.JOB_STORE_TX);
        factory.setQuartzProperties(quartzProperties());
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        factory.setOverwriteExistingJobs(true);
        factory.setStartupDelay(1);
        factory.setAutoStartup(true);
        return factory;
    }

    @Bean
    public Properties quartzProperties() {
        Properties prop = new Properties();
        prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        prop.put("org.quartz.jobStore.dataSource", "myDS");
        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        prop.put("org.quartz.jobStore.isClustered", "true");
        prop.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
        prop.put("org.quartz.dataSource.myDS.driver", "org.postgresql.Driver");
        prop.put("org.quartz.dataSource.myDS.URL", "jdbc:postgresql://localhost:5432/yourdb");
        prop.put("org.quartz.dataSource.myDS.user, youruser");
        prop.put("org.quartz.dataSource.myDS.password", "yourpassword");
        prop.put("org.quartz.dataSource.myDS.maxConnections", "5");
        return prop;
    }
}
