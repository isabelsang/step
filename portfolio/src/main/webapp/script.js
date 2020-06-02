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

/**
 * Adds a random famous person from New Jersey to page.
 */
function addRandomNJPerson() {
  //show container
  const personContainer = document.getElementById("NJ-person-container");
  personContainer.style.display = "block"

  const people =
      ['Bruce Springsteen', 'Frank Sinatra', 'Meryl Streep', 'Whitney Houston', 'Queen Latifah'];
  const descriptions = 
      ['Boooooorn in the (best state in the) U.S.A.', 'Not sure why he never made a song about New Jersey, New Jersey',
      'She is from the town adjacent to mine!', 'She Will Always Love New Jersey', 'Royalty!'];

  // Pick a random number in order to pick a random person and their respective description.
  const randomNum = Math.floor(Math.random() * people.length);
  const person = people[randomNum];
  const descrip = descriptions[randomNum];

  // Add it to the page.
  const personNameContainer = document.getElementById('NJ-person-name');
  const personDescripContainer = document.getElementById('NJ-person-descrip');
  personNameContainer.innerText = person;
  personDescripContainer.innerText = descrip;
}

/**
 * Closes the randomizer or the popup
 */
function closeWindow(elementID){
  document.getElementById(elementID).style.display = "none";
}

/**
 * Makes image larger and displays in popup.
 */
function makeLarger(pic){
  /* display popup */
  document.getElementById("popup").style.display = "block";

  /* display photo */
  const popupPhoto = document.getElementById("popup-photo");
  popupPhoto.src = pic.src;
  popupPhoto.alt = pic.alt;

  /* display descrip */
  const popupDescrip = document.getElementById("popup-descrip");
  popupDescrip.innerHTML = pic.alt;
}

function getFromDataServlet(){
    fetch('/data').then(response => response.text()).then((message) => {
    document.getElementById('message-container').innerHTML = message;
  });
}
