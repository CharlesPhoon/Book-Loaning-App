package com.example.loanbookapp

data class Loan(
    val userId: String? = null,
    val userName: String? = null,
    val studentId: String? = null,
    val bookName: String? = null,
    val author: String? = null,
    val loanDateTime: String? = null,
    val loanPeriodDays: Int? = null
)
