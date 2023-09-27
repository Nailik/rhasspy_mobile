package org.rhasspy.mobile.logic.connections.user

import kotlinx.coroutines.flow.Flow

interface IUserConnection {

    val incomingMessages: Flow<UserConnectionEvent>

}

class UserConnection : IUserConnection {
}