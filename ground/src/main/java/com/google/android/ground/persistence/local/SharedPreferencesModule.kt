/*
 * Copyright 2023 Google LLC
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
package com.google.android.ground.persistence.local

import android.content.Context
import android.content.SharedPreferences
import com.google.android.ground.Config
import com.google.android.ground.persistence.local.room.dao.*
import com.google.android.ground.persistence.local.room.stores.*
import com.google.android.ground.persistence.local.stores.*
import com.google.android.ground.util.allowThreadDiskReads
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object SharedPreferencesModule {
  @Provides
  @Singleton
  fun sharedPreferences(@ApplicationContext context: Context): SharedPreferences =
    allowThreadDiskReads {
      context.getSharedPreferences(Config.SHARED_PREFS_NAME, Config.SHARED_PREFS_MODE)
    }
}
