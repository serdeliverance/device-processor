package com.challenge.deviceprocessor

import java.util.UUID
import java.time.Instant

case class Device(deviceId: UUID, name:String, createdAt: Instant)