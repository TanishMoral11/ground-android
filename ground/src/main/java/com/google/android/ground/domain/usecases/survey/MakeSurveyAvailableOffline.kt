/*
 * Copyright (c) 2023 Google LLC
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

package com.google.android.ground.domain.usecases.survey

import com.google.android.ground.model.Survey
import com.google.android.ground.repository.SurveyRepository

class MakeSurveyAvailableOffline(
  private val surveyRepository: SurveyRepository,
  private val syncSurvey: SyncSurveyUseCase
) {
  /**
   * Makes the survey with the specified ID and related LOIs available offline. Subscribes to
   * updated from the remote server so that they may be refetched on updates.
   */
  suspend operator fun invoke(surveyId: String): Survey {
    val survey = syncSurvey(surveyId)
    surveyRepository.subscribeToSurveyUpdates(surveyId)
    return survey
  }
}
