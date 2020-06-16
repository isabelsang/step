// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> attendees = request.getAttendees();
    long duration = request.getDuration();
    Collection<TimeRange> times;

    //Case in which the requested meeting has no attendies
    if(attendees.isEmpty()){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    //Case in which the requested meeting has a duration that is too long/short
    if(duration > TimeRange.WHOLE_DAY.duration() || duration < 0){
        return Arrays.asList();
    } 

    //New collection of events that consist of events that the attendees are attending 
    //and ignore ones that they are not
    Collection<Event> relevantEvents = new HashSet<>();
    for(String person : attendees){
        for(Event event: events){
            if(event.getAttendees().contains(person)){
                relevantEvents.add(event);
            }
        }
    }

    if(relevantEvents.isEmpty()){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    
    return null;
  }
}
