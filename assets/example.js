//FightCode can only understand your robot
//if its class is called Robot
var Robot = function(robot) {
  
};

Robot.prototype.onIdle = function(ev) {
  var robot = ev.robot;
  robot.ahead(100);
  robot.rotateCannon(360);
  robot.back(100);
  robot.rotateCannon(360);
};

Robot.prototype.onScannedRobot = function(ev) {
  var robot = ev.robot;
  robot.fire();
};