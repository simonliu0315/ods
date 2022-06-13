/*jslint white: true, browser: true, undef: true, nomen: true, eqeqeq: true, plusplus: false, bitwise: true, regexp: true, strict: true, newcap: true, immed: true, maxerr: 14 */
/*global window: false, REDIPS: true */

/* enable strict mode */
"use strict";

// create redips container
var redips = {};

// redips initialization
redips.init = function () {
	// reference to the REDIPS.drag object
	var	rd = REDIPS.drag;
	// define border style (this should go before init() method)
	rd.style.borderEnabled = 'none';
	// initialization
	rd.init();
	// set hover color
	rd.hover.colorTd = '#9BB3DA';
	// DIV elements can be dropped to the empty cells only
	rd.dropMode = 'single';

	rd.event.cloned = function () {
		// set id of cloned element
		var clonedId = rd.obj.id;
		
		// if id of cloned element begins with "e" then make exception (allow DIV element to access cells with class name "mark")
		if (clonedId.substr(0, 1) === 'a') {   
			rd.mark.exception[clonedId] = 'mark';
		}
		if (clonedId.substr(0, 1) === 'c') {   
			rd.mark.exception[clonedId] = 'mark';
		}	
		if (clonedId.substr(0, 1) === 'b') {   
			rd.mark.exception[clonedId] = 'mark';
		}
		if (clonedId.substr(0, 1) === 'd') {   
			rd.mark.exception[clonedId] = 'mark';
		}
		if (clonedId.substr(0, 1) === 'e') {   
			rd.mark.exception[clonedId] = 'mark';
		}
	};
};

// add onload event listener
if (window.addEventListener) {
	window.addEventListener('load', redips.init, false);
}
else if (window.attachEvent) {
	window.attachEvent('onload', redips.init);
}