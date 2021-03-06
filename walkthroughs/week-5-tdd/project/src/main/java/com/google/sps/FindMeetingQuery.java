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
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.*;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> mandatory = request.getAttendees();
    Collection<String> optional = request.getOptionalAttendees();
    Collection<String> attendees = mandatory; 
    long duration = request.getDuration();

    boolean optionalAttendeesInRequest = true;
    if(optional.isEmpty()){
        optionalAttendeesInRequest = false;
    } else {
        attendees.addAll(optional);
    }

    //Case in which the requested meeting has no attendies
    if(attendees.isEmpty()){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    //Case in which the requested meeting has a duration that is too long/short
    if(duration > TimeRange.WHOLE_DAY.duration() || duration < 0){
        return Arrays.asList();
    } 

    //only are concerned with events that attendees are going to 
    Collection<TimeRange> timeRangesSet = getRelevantEvents(attendees, events);

    //Case in which no events on calendar so no conflicts
    if(timeRangesSet.isEmpty()){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    List<TimeRange> timeRanges = new ArrayList<TimeRange>(timeRangesSet);
    addDummyEvents(timeRanges);
    Collections.sort(timeRanges, TimeRange.ORDER_BY_START);

    Collection<TimeRange> availTimes = new ArrayList<TimeRange>(); //Time ranges that are available for the meeting request
    
    TimeRange lastTimeRange = timeRanges.get(0); //Last time range that has been accounted for from timeRanges
    TimeRange nextTimeRange; //Next time range that needs to be accounted for
    int timeRangesCtr = 1; 

    //timeRangesCtr: Index of current position in array list "timeRanges", 
    //start at 1 since timeRange at index 0 is dummy and 
    //has already been accounted for
    int startOfAvail = lastTimeRange.end();
    int endOfAvail; 

    while(startOfAvail < TimeRange.END_OF_DAY && timeRangesCtr < timeRanges.size()){ 
        nextTimeRange = timeRanges.get(timeRangesCtr);
        if (lastTimeRange.contains(nextTimeRange)){
            lastTimeRange = nextTimeRange;
            timeRangesCtr++;
        } else if(lastTimeRange.overlaps(nextTimeRange)) {
            startOfAvail = nextTimeRange.end();
            timeRangesCtr++;
        } else {
            endOfAvail = nextTimeRange.start();
            if((endOfAvail - startOfAvail) >= duration){
                //Enough available time for the meeting so add it to the array list
                availTimes.add(TimeRange.fromStartEnd(startOfAvail, endOfAvail, false));
            }

            lastTimeRange = nextTimeRange;
            startOfAvail = nextTimeRange.end();
            timeRangesCtr++;
        }
    }

    if(optionalAttendeesInRequest && availTimes.isEmpty()){
        //If attempt to include optional attendees resulted in no time slots, try with just mandatory attendees
        return query(events, new MeetingRequest(mandatory, duration));
    } else {
        return availTimes;
    }
  }

  //Returns time ranges of other events that attendees are attending
  public Collection<TimeRange> getRelevantEvents(Collection<String> attendees, Collection<Event> events){
      
    Collection<TimeRange> timeRangesSet = new HashSet<TimeRange>();

    events.stream()
          .filter(
            event -> attendees.stream()
                        .anyMatch(person -> event.getAttendees().contains(person))
            )
          .forEach(event -> timeRangesSet.add(event.getWhen()));

    return timeRangesSet;
  }

  public List<TimeRange> addDummyEvents(List<TimeRange> timeRanges){
    timeRanges.add(TimeRange.fromStartDuration(TimeRange.START_OF_DAY, 0));
    timeRanges.add(TimeRange.fromStartDuration(TimeRange.END_OF_DAY + 1, 0));
    return timeRanges;
  }
}
