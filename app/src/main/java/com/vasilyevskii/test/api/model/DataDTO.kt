package com.vasilyevskii.test.api.model

class DataDTO{
    var id: Int
    set(valueId) {
        id = valueId
    }
    get() {
        return id
    }


    var target: String
    set(valueTarget) {
        target = valueTarget
    }
    get() {
        return target
    }

}