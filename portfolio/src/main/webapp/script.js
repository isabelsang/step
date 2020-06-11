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
  //Show container
  const personContainer = document.getElementById('NJ-person-container');
  personContainer.style.display = 'block';

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
  document.getElementById(elementID).style.display = 'none';
}

/**
 * Makes image larger and displays in popup.
 */
function makeLarger(pic){
  /* Display popup */
  document.getElementById('popup').style.display = 'block';

  /* Display photo */
  const popupPhoto = document.getElementById('popup-photo');
  popupPhoto.src = pic.src;
  popupPhoto.alt = pic.alt;

  /* Display descrip */
  const popupDescrip = document.getElementById('popup-descrip');
  popupDescrip.innerHTML = pic.alt;
}

/**
 * Fetches comments from data servlet
 */
function getComments(){
    checkLoginStatus();
    const commentsContainer = document.getElementById('comments-container');
    commentsContainer.innerHTML = '';

    const commentLimit = getCommentLimit();
    const fetchURL = '/data?comment-limit=' + commentLimit;

    fetch(fetchURL).then(response => response.json()).then((comments) => {
    for (i = 0; i < comments.length; i++){
         commentsContainer.appendChild(createComment(comments[i]));
    }  
  });
}

/** Creates a <p> element with class .comment containg text passed as parameter */
function createComment(comment){
    // Container div
    const commentElement = document.createElement('div');
    commentElement.classList.add('comment');

    const textDivElement = document.createElement('div');
    textDivElement.classList.add('comment-text');

    const nameElement = document.createElement('p');
    nameElement.innerText = comment.name;
    nameElement.classList.add('comment-name');

    const emailElement = document.createElement('p');
    emailElement.innerText = comment.email;
    emailElement.classList.add('comment-email');

    const messageElement = document.createElement('p');
    messageElement.innerText = comment.message;
    messageElement.classList.add('comment-message');

    const deleteBtnElement = document.createElement('button');
    deleteBtnElement.innerText = 'Delete';
    deleteBtnElement.classList.add('delete-comment-btn');
    deleteBtnElement.addEventListener('click', () => {
      deleteComment(comment);

      //Remove from DOM 
      commentElement.remove();
    });

    const moodElement = document.createElement('img');
    moodElement.src = '/images/moods/' + comment.mood + '.png';
    moodElement.alt = comment.mood;
    moodElement.classList.add('comment-mood');

    textDivElement.appendChild(nameElement);
    textDivElement.appendChild(emailElement);
    textDivElement.appendChild(messageElement);
    commentElement.appendChild(moodElement);
    commentElement.appendChild(textDivElement);
    commentElement.appendChild(deleteBtnElement);
    

    return commentElement;
}

/** Gets user-inputted comment limit  */
function getCommentLimit(){
    const limit = document.getElementById('comment-limit-select').value;
    return limit;
}

/** Passes server id of comment to delete  */
function deleteComment(comment){
    const params = new URLSearchParams();
    params.append('id', comment.id);
    fetch('/delete-data', {method: 'POST', body: params}).then(getComments());
}

/** Checks if user is logged in or out */
function checkLoginStatus(){
    fetch('/login-status').then(response => response.json()).then(loginStatus => {
        // Convert isUserLoggedIn from server from string to bool 
        const stringIsUserLoggedIn = loginStatus.isUserLoggedIn;
        const boolIsUserLoggedIn = (stringIsUserLoggedIn === 'true');
        
        // Get the comment form element to either hide or display 
        const commentFormElement = document.getElementById('comment-form');

        // Create hyperlink element to login or logout 
        const linkElement = document.createElement('a');
        linkElement.href = loginStatus.url;
        linkElement.classList.add('login-link');

        // Display correct elements based on whether user is logged in or not
        if(boolIsUserLoggedIn){
            linkElement.innerText = 'Logout here';
            commentFormElement.style.display = 'block';
        } else {
            linkElement.innerText = 'Login here';
            commentFormElement.style.display = 'none';
        }

        // Add hyperlink element to DOM
        const loginContainer = document.getElementById('login-container');
        loginContainer.innerHTML = ''; //clear it first so doesn't duplicate
        loginContainer.appendChild(linkElement);
    });
}

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/** Creates chart and displays it */
function drawChart() {
  const data = new google.visualization.DataTable();
  data.addColumn('string', 'Ways to make eggs');
  data.addColumn('number', 'Count');
    data.addRows([
        ['Scrambled', 10],
        ['Sunny side up', 6],
        ['Hard boiled', 4],
        ['In a breakfast sandwich', 7],
        ['Omelet', 5]
    ]);
  
  const options = {
    'title': 'Favorite ways to make eggs',
    'width': 500,
    'height': 400, 
    'colors': ['#0F1E3D','#264B96','#366BD6','#3D91F2','#84C0F5'],
    'backgroundColor': 'transparent'
  };

  const chart = new google.visualization.PieChart(document.getElementById('chart-container'));
  chart.draw(data, options);
}
