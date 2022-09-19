package com.decomposepermissions.home.ui

import com.arkivanov.decompose.ComponentContext

class RealHomeComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, HomeComponent
