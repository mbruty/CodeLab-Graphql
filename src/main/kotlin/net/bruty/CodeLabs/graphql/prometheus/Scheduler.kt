package net.bruty.CodeLabs.graphql.prometheus

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import org.springframework.stereotype.Component

@Component
class Scheduler(meterRegistry: MeterRegistry) {

    init {
        // meterRegistry.gaugeCollectionSize("gt_queue_size", Tags.of("name", "highest"), data[0])
    }
}