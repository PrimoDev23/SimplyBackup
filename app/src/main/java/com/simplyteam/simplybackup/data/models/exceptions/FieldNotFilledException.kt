package com.simplyteam.simplybackup.data.models.exceptions

class FieldNotFilledException constructor(
    fieldName : String
) : Exception(
    fieldName
)