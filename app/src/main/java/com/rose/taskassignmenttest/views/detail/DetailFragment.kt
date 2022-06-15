package com.rose.taskassignmenttest.views.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rose.taskassignmenttest.R
import com.rose.taskassignmenttest.data.Task
import com.rose.taskassignmenttest.utils.TimeUtils
import com.rose.taskassignmenttest.viewmodels.DetailViewModel
import com.rose.taskassignmenttest.viewmodels.fakers.FakeTaskDao
import com.rose.taskassignmenttest.views.common.StatusTagView

class DetailFragment : Fragment() {
    private lateinit var mViewModel: DetailViewModel

    private lateinit var mTitleText: EditText
    private lateinit var mCheck: CheckBox
    private lateinit var mDeadlineText: TextView
    private lateinit var mStatusTagView: StatusTagView
    private lateinit var mCreateTimeText: TextView
    private lateinit var mModifiedTimeText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_detail, container, false)

        mTitleText = root.findViewById(R.id.detail_title)
        mCheck = root.findViewById(R.id.detail_check)
        mDeadlineText = root.findViewById(R.id.detail_deadline)
        mStatusTagView = root.findViewById(R.id.detail_status_tag)
        mCreateTimeText = root.findViewById(R.id.detail_create_text)
        mModifiedTimeText = root.findViewById(R.id.detail_modified_text)
        val deadlineContainer: View = root.findViewById(R.id.detail_deadline_container)
        val statusContainer: View = root.findViewById(R.id.detail_status_container)

        activity?.let {
            mViewModel = ViewModelProvider(it)[DetailViewModel::class.java]
            mViewModel.setTaskDao(FakeTaskDao())
            mViewModel.getTask().observe(it) { task -> updateTask(task) }
            mViewModel.getIsSaveSuccess().observe(it) { isSaved -> onTaskSaved(isSaved) }

            mViewModel.loadTask()
            root?.let { view ->
                view.findViewById<Button>(R.id.detail_save_btn)
                    .setOnClickListener {
                        updateTitleAndChecked()
                        mViewModel.saveTask()
                    }
            }
        }

        statusContainer.setOnClickListener {
            val selectDialog = StatusSelectDialog()

            val locationWindow = intArrayOf(-1, -1)
            mStatusTagView.getLocationInWindow(locationWindow)

            selectDialog.setPosition(locationWindow[0] - mStatusTagView.width / 2,
                locationWindow[1] - mStatusTagView.height / 2)
            selectDialog.show(requireActivity().supportFragmentManager, StatusSelectDialog.TAG)
        }

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        updateTitleAndChecked()
    }

    private fun updateTitleAndChecked() {
        mViewModel.updateTaskData(mTitleText.text.toString(), mCheck.isChecked)
    }

    private fun updateTask(task: Task) {
        context?.let {
            mTitleText.setText(task.title)
            mTitleText.setSelection(task.title.length)
            mCheck.isChecked = task.completed
            mDeadlineText.text = if (task.deadLine == -1L) it.getString(R.string.no_end_date)
            else TimeUtils.getDateTime(task.deadLine)
            mStatusTagView.setStatus(task.status)
            mCreateTimeText.text = it.getString(R.string.created_time, TimeUtils.getDateTime(task.createdTime))
            mModifiedTimeText.text = it.getString(R.string.last_modified_time, TimeUtils.getDateTime(task.modifiedTime))
        }
    }

    private fun onTaskSaved(isSaved: Boolean) {
        activity?.let {
            Toast.makeText(
                it, it.getString(if (isSaved) R.string.task_saved else R.string.failed_to_save),
                Toast.LENGTH_SHORT
            ).show()
            it.finish()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DetailFragment()
        private const val TAG = "DetailFragment"
    }
}