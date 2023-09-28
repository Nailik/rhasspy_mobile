package org.rhasspy.mobile.logic.connections.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface IUserConnection {

    val incomingMessages: Flow<UserConnectionEvent>

}

class UserConnection : IUserConnection {

    override val incomingMessages = MutableSharedFlow<UserConnectionEvent>()

    //TODO
}