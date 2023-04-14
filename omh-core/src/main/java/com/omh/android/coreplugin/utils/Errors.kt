package com.omh.android.coreplugin.utils

internal val ERROR_DEFAULTSERVICES_AT_LEAST_ONE = """
You must define at least one service in the default services section
""".trim()

internal val ERROR_DEFAULTSERVICES_BUT_NO_BUNDLES = """
Default services are defined, but there are not bundles
""".trim()

internal val ERROR_DEFAULTSERVICES_ARE_NOT_IN_BUNDLES = """
At least one default service is not defined in bundles
""".trim()

internal val ERROR_BUNDLES_WITHOUT_NAME = """
There is one bundle without a name
""".trim()

internal val ERROR_BUNDLES_WITH_INCORRECT_NAME = """
There is one bundle with incorrect name, use same pattern used to name build variants
""".trim()

internal val ERROR_BUNDLES_BUT_NO_SERVICES = """
At least one bundle is empty, it doesn't have services defined
""".trim()
