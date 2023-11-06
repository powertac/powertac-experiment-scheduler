package org.powertac.rachma.logprocessor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogProcessorConfig {

    @Bean
    public LogProcessorProvider logProcessorProvider() {
        final ConfigurableLogProcessorProvider provider = new ConfigurableLogProcessorProvider();
        provider.addProcessor(new LogProcessor("tariff-transaction-counter", "org.powertac.logprocessor.tariff.TariffTransactionCounter"));
        provider.addProcessor(new LogProcessor("weather-reports", "org.powertac.logprocessor.weather.WeatherReports"));
        provider.addProcessor(new LogProcessor("tariff-transactions", "org.powertac.logprocessor.tariff.TariffTransactions"));
        provider.addProcessor(new LogProcessor("balancing-market-transactions", "org.powertac.logprocessor.market.BalancingMarketTransactions"));
        return provider;
    }

}
