/*
	figures.js
*/

function Point (x, y) {
	this.x = x;
	this.y = y;
}	

function testBetween() {
    //
}

// Method getX() 
Point.prototype.getX = function() {
    return this.x;
}

// Method setX() 
Point.prototype.setX = function(x) {
    return this.x = x;
}

function test() {}