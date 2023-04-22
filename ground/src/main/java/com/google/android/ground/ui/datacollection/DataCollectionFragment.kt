/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.ground.ui.datacollection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.ground.R
import com.google.android.ground.databinding.DataCollectionFragBinding
import com.google.android.ground.model.submission.TaskData
import com.google.android.ground.model.task.Task
import com.google.android.ground.rx.Schedulers
import com.google.android.ground.ui.common.AbstractFragment
import com.google.android.ground.ui.common.BackPressListener
import com.google.android.ground.ui.common.Navigator
import dagger.hilt.android.AndroidEntryPoint
import java8.util.Optional
import javax.inject.Inject

/** Fragment allowing the user to collect data to complete a task. */
@AndroidEntryPoint
class DataCollectionFragment : AbstractFragment(), BackPressListener {
  @Inject lateinit var navigator: Navigator
  @Inject lateinit var schedulers: Schedulers
  @Inject lateinit var viewPagerAdapterFactory: DataCollectionViewPagerAdapterFactory

  private val args: DataCollectionFragmentArgs by navArgs()
  private val viewModel: DataCollectionViewModel by hiltNavGraphViewModels(R.id.data_collection)

  private lateinit var viewPager: ViewPager2
  private lateinit var progressBar: ProgressBar

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreateView(inflater, container, savedInstanceState)
    val binding = DataCollectionFragBinding.inflate(inflater, container, false)
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    viewPager = binding.pager
    progressBar = binding.progressBar
    getAbstractActivity().setActionBar(binding.dataCollectionToolbar, showTitle = false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewPager.isUserInputEnabled = false
    viewPager.offscreenPageLimit = 1

    viewModel.loadSubmissionDetails(args)
    viewModel.submission.observe(viewLifecycleOwner) { loadTasks(it.job.tasksSorted) }
    viewModel.currentPosition.observe(viewLifecycleOwner) { onTaskChanged(it) }
    viewModel.currentTaskDataLiveData.observe(viewLifecycleOwner) { onTaskDataUpdated(it) }
  }

  private fun loadTasks(tasks: List<Task>) {
    val currentAdapter = viewPager.adapter as? DataCollectionViewPagerAdapter
    if (currentAdapter == null || currentAdapter.tasks != tasks) {
      viewPager.adapter = viewPagerAdapterFactory.create(this, tasks)
    }

    // Reset progress bar
    progressBar.progress = 0
    progressBar.max = tasks.size
  }

  private fun onTaskChanged(index: Int) {
    viewPager.currentItem = index
    progressBar.progress = index
  }

  private fun onTaskDataUpdated(taskData: Optional<TaskData>) {
    viewModel.currentTaskData = taskData.orElse(null)
  }

  override fun onBack(): Boolean =
    if (viewPager.currentItem == 0) {
      // If the user is currently looking at the first step, allow the system to handle the
      // Back button. This calls finish() on this activity and pops the back stack.
      false
    } else {
      // Otherwise, select the previous step.
      viewModel.setCurrentPosition(viewModel.currentPosition.value!! - 1)
      true
    }
}
