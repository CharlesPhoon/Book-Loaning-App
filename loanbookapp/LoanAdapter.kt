package com.example.loanbookapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class LoanAdapter : ListAdapter<Loan, LoanAdapter.LoanViewHolder>(LoanDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan, parent, false)
        return LoanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val currentLoan = getItem(position)
        holder.bind(currentLoan)
    }

    class LoanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        private val studentIdTextView: TextView = itemView.findViewById(R.id.studentIdTextView)
        private val bookNameTextView: TextView = itemView.findViewById(R.id.bookNameTextView)
        private val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)
        private val loanDateTimeTextView: TextView = itemView.findViewById(R.id.loanDateTimeTextView)
        private val loanPeriodDaysTextView: TextView = itemView.findViewById(R.id.loanPeriodDaysTextView)

        fun bind(loan: Loan) {
            userNameTextView.text = loan.userName
            studentIdTextView.text = loan.studentId
            bookNameTextView.text = loan.bookName
            authorTextView.text = loan.author
            loanDateTimeTextView.text = loan.loanDateTime
            loanPeriodDaysTextView.text = loan.loanPeriodDays.toString()
        }
    }

    private class LoanDiffCallback : DiffUtil.ItemCallback<Loan>() {
        override fun areItemsTheSame(oldItem: Loan, newItem: Loan): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Loan, newItem: Loan): Boolean {
            return oldItem == newItem
        }
    }
}