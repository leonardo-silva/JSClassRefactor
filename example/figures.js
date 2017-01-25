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

// Point 3D
function Point3D (x, y, z) {
	Point.call(this, x, y);
	this.z = z;
}
Point3D.prototype = new Point();

function test() {}