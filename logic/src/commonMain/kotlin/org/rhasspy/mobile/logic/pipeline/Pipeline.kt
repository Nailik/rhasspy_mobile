package org.rhasspy.mobile.logic.pipeline

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent
import org.rhasspy.mobile.logic.pipeline.manager.PipelineManagerDisabled
import org.rhasspy.mobile.logic.pipeline.manager.PipelineManagerLocal
import org.rhasspy.mobile.logic.pipeline.manager.PipelineManagerMqtt
import org.rhasspy.mobile.platformspecific.IDispatcherProvider
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IPipeline {

    val pipelineHistory: StateFlow<List<PipelineState>>

    fun onEvent(event: PipelineEvent)
    fun onEvent(event: MqttConnectionEvent)
    fun onEvent(event: WebServerConnectionEvent)

}

internal class Pipeline(
    dispatcherProvider: IDispatcherProvider,
) : KoinComponent, IPipeline {

    private val pipelineManager
        get() = when (ConfigurationSetting.pipelineData.value.option) {
            DialogManagementOption.Local              -> get<PipelineManagerLocal>()
            DialogManagementOption.Rhasspy2HermesMQTT -> get<PipelineManagerMqtt>()
            DialogManagementOption.Disabled           -> get<PipelineManagerDisabled>()
        }

    private val scope = CoroutineScope(dispatcherProvider.IO)

    init {
        scope.launch {
            ConfigurationSetting.pipelineData.data.collectLatest {
                pipelineManager.initialize()
            }
        }
    }

    override val pipelineHistory get() = pipelineManager.pipelineHistory

    override fun onEvent(event: PipelineEvent) = pipelineManager.onEvent(event)
    override fun onEvent(event: MqttConnectionEvent) = pipelineManager.onEvent(event)
    override fun onEvent(event: WebServerConnectionEvent) = pipelineManager.onEvent(event)

}