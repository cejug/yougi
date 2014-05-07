/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */

var attendees = [];
var position = -1;
var interval = null;
var timeout = null;

function loadAttendees( xhr , status , data ) {
	attendees = JSON.parse(data.attendees);
	btnLoadAttendees.disable();
	btnInitRaffle.enable();
	wgtRaffleDlg.show();
}

function clearRaffle(){
	cleanAllRunningIntervals();
	attendees = [];
	btnLoadAttendees.enable();
	btnInitRaffle.disable();
	btnRemoveLastWinner.disable();
}

function cleanAllRunningIntervals(){
	if (interval != null){
		clearInterval(interval);
		interval = null;
	}
	if( timeout != null){
		clearTimeout(timeout);
		timeout = null;
	}
}

function initRaffle() {

	var changeFrameRate = 100;
	var timer = 5;
	var timerCounter = 0;	
	var length = attendees.length;
	var timerElement = $("#timer")[0];
	
	function changeTimerValue(newValue){
		timerElement.innerText = newValue;
	}
	
	function changeAtendee() {
		incrementTimer();
		position = parseInt(Math.random() * (length));
		$("#winnerName").text( attendees[position].name );
	}
	
	function incrementTimer(){
		timerCounter += changeFrameRate;
		if (timerCounter >= 1000) {
			timerCounter = 0;
			timer--;
			changeTimerValue(timer);
		}
	}
	
	function setRaffleEndRountine(){
		timeout = setTimeout(function() {
			cleanAllRunningIntervals();
			changeTimerValue("0");
			var i = 0;
			var arrAnnimation = [ "<o>" ,  "\\o/"];
			function stupidit() {
				changeTimerValue( arrAnnimation[i] );
				i = ( i > 0 ) ? 0 : 1;
			}
			interval = setInterval(stupidit, 700);
			
			btnRemoveLastWinner.enable();
		}, 5000);
	}
	
	function raffle(){
		cleanAllRunningIntervals();
		changeTimerValue(timer);
		interval = setInterval(changeAtendee, changeFrameRate);
		setRaffleEndRountine();
	}
	
	raffle();
}

function removeLastWinner() {
	attendees.splice(position, 1);
}