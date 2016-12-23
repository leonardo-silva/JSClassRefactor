/*
	figures.js
*/

function Point (x, y) {
	this.x = x;
	this.y = y;
}	
// Method getX() 
Point.prototype.getX = function() {
    return this.x;
}