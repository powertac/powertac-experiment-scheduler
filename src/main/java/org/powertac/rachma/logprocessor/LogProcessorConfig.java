package org.powertac.rachma.logprocessor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogProcessorConfig {

    @Bean
    public LogProcessorProvider logProcessorProvider() {
        final ConfigurableLogProcessorProvider provider = new ConfigurableLogProcessorProvider();
        provider.addProcessor(new LogProcessor(
            "weather-reports",
            "org.powertac.logprocessor.weather.WeatherReports",
            "%s.weather-reports.csv"));
        provider.addProcessor(new LogProcessor(
            "tariff-transactions",
            "org.powertac.logprocessor.tariff.TariffTransactions",
            "%s.tariff-transactions.csv"));
        provider.addProcessor(new LogProcessor(
            "balancing-market-transactions",
            "org.powertac.logprocessor.market.BalancingMarketTransactions",
            "%s.balancing-market-transactions.csv"));
        return provider;
    }

}
