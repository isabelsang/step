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
    Collection<String> attendees = request.getAttendees();
    long duration = request.getDuration();

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

    //Change to ArrayList and sort timeRanges by starting time 
    List<TimeRange> timeRanges = new ArrayList<TimeRange>(timeRangesSet);
    Collections.sort(timeRanges, TimeRange.ORDER_BY_START);

    Collection<TimeRange> availTimes = new ArrayList<TimeRange>(); //Time ranges that are available for the meeting request
    int startOfAvail = TimeRange.START_OF_DAY; //The start of an available time range
    int endOfAvail; 
    int timeRangesCtr = 0; //Index of current position in array list "timeRanges"
    TimeRange lastTimeRange = null; //Last time range that has been accounted for from timeRanges
    TimeRange nextTimeRange = timeRanges.get(0);; //Next time range that needs to be accounted for

    if(nextTimeRange.start() == TimeRange.START_OF_DAY){
        //If earliest event starts at 00:00 then update startOfAvail 
        //to be the end time of the first event
        startOfAvail = nextTimeRange.end();

        //First event is accounted for, so update lastTimeRange
        lastTimeRange = nextTimeRange;
        timeRangesCtr++;
    } 

    while(startOfAvail < TimeRange.END_OF_DAY && timeRangesCtr < timeRanges.size()){ 
        nextTimeRange = timeRanges.get(timeRangesCtr);

        if((null != lastTimeRange) && (lastTimeRange.overlaps(nextTimeRange))) {
            startOfAvail = nextTimeRange.end();
            timeRangesCtr++;
        } else if ((null != lastTimeRange) && (lastTimeRange.contains(nextTimeRange))){
            lastTimeRange = nextTimeRange;
            timeRangesCtr++;
        } else {
            endOfAvail = nextTimeRange.start() - 1;
            if((endOfAvail - startOfAvail) >= duration){
                //Enough available time for the meeting so add it to the array list
                availTimes.add(TimeRange.fromStartEnd(startOfAvail, endOfAvail, true));
            }

            lastTimeRange = nextTimeRange;
            startOfAvail = nextTimeRange.end();
            timeRangesCtr++;
        }

        //Check if there is enough time for requested meeting after last event
        endOfAvail = TimeRange.END_OF_DAY;
        if((startOfAvail < TimeRange.END_OF_DAY) && ((endOfAvail - startOfAvail) >= duration)){
           availTimes.add(TimeRange.fromStartEnd(startOfAvail, endOfAvail, true)); 
        }
    }

    return availTimes;
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
}
