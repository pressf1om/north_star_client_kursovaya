package com.northstar.app

object AuthRoutes {
    const val SIGN_IN = "signin"
    const val REGISTER = "register"
}

object MainRoutes {
    const val ADMIN_PEOPLE = "admin/people"
    const val ADMIN_ASSIGN_ROLE = "admin/assign_role/{userId}"

    fun adminAssignRole(userId: Int): String = "admin/assign_role/$userId"
    const val ADMIN_FLEET = "admin/fleet"
    const val NEW_APPLICATION = "fleet/new_application"
    const val ADMIN_MORE = "admin/more"

    const val DISPATCH_FLEET = "dispatch/fleet"
    const val DISPATCH_ARCHIVE = "dispatch/archive"
    const val DISPATCH_MORE = "dispatch/more"
    const val DISPATCH_MESSAGES = "dispatch/messages"

    const val DRIVER_TRIP = "driver/trip"
    const val DRIVER_ARCHIVE = "driver/archive"
    const val DRIVER_MORE = "driver/more"
    const val DRIVER_HELP = "driver/help"
}
