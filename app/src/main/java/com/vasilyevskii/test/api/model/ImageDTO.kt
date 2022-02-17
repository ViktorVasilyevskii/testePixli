package com.vasilyevskii.test.api.model

import java.io.File

data class ImageDTO (
        val id: Int,
        val image: File,
        val contact: Array<String>
        ) {

}