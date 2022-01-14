package com.graveno.alphalab.app.codedemo

data class MainRcModel(
    var serverEntry : String?,
    var options : ArrayList<SubMain>,
    // selected list
    var selectedOption : ArrayList<SubMain> = ArrayList()
) {
    data class SubcModel(
        var UserInput : String?,
        var userSelection : String?,
    )

    data class SubMain(
        var userInput : String?,
        var userSelection : String?,
        var id : Int?,
        var serverEntry : String?,
        var name : String?
    )
}