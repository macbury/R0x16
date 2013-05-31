
Robot = function(index) {

    this.dataIndex = index;
    this.group = new THREE.Object3D();
    this.color = 0xFFFFFF;
    this.index = 0;
    this.expectedIndex = 0;

    this.availableIndicies = new Array();
    for (var i = 0; i < DATA.statements.length; i++) {
        this.availableIndicies.push(i);
    }
    utils.shuffle(this.availableIndicies);

    this.neutralSpec = DATA.neutralSpec;

    path = DATA.robot[this.dataIndex].path;
    ext = DATA.robotExt;
    mat = new THREE.MeshPhongMaterial({color: this.color});
    mat.shading = THREE.SmoothShading;

    this.addToScene();
}

Robot.prototype.resetAll = function() {
    this.availableIndicies = new Array();
    for (var i = 0; i < DATA.statements.length; i++) {
        this.availableIndicies.push(i);
    }
    utils.shuffle(this.availableIndicies);
}

Robot.prototype.getValuesFromGui = function() {
    values = new Array();
    values.push(document.getElementById("robotGuiA").value * 0.01);
    values.push(document.getElementById("robotGuiB").value * 0.01);
    values.push(document.getElementById("robotGuiC").value * 0.01);
    values.push(document.getElementById("robotGuiD").value * 0.01);
    values.push(document.getElementById("robotGuiE").value * 0.01);
    values.push(document.getElementById("robotGuiF").value * 0.01);
    values.push(document.getElementById("robotGuiG").value * 0.01);
    values.push(document.getElementById("robotGuiH").value * 0.01);
    values.push(document.getElementById("robotGuiI").value * 0.01);
    values.push(document.getElementById("robotGuiJ").value * 0.01);
    values.push(document.getElementById("robotGuiK").value * 0.01);
    return values;
}

Robot.prototype.addToScene = function() {
    // Static parts
    for (var i = 0; i < DATA.robot[this.dataIndex].staticParts.length; i++) {
        spec = DATA.robot[this.dataIndex].staticParts[i];
        part = spec.part;

        v = new THREE.Vector3(spec.offset[0], spec.offset[1], spec.offset[2]);
        utils.loadFromFile(path + "/" + part + "." + ext, mat, v, this.group, this, part);
    }

    // Group parts
    for (var i = 0; i < DATA.robot[this.dataIndex].groupParts.length; i++) {
        spec = DATA.robot[this.dataIndex].groupParts[i];
        currGroup = new THREE.Object3D();
        currGroup.position = new THREE.Vector3(spec.offset[0], spec.offset[1], spec.offset[2]);

        for (var j = 0; j < spec.children.length; j++) {
            part = spec.children[j].part;
            v = new THREE.Vector3(spec.children[j].offset[0], spec.children[j].offset[1], spec.children[j].offset[2]);
            utils.loadFromFile(path + "/" + part + "." + ext, mat, v, currGroup, this, part);    
        }

        this[spec.name] = currGroup;
        this.group.add(currGroup);
    }

    // Dynamic parts
    for (var i = 0; i < DATA.robot[this.dataIndex].dynamicParts.length; i++) {

        spec = DATA.robot[this.dataIndex].dynamicParts[i];
        part = spec.part;

        console.log('' + part + ' part created');

        v = new THREE.Vector3(spec.offset[0], spec.offset[1], spec.offset[2]);

        utils.loadFromFileWithBlends(
            path + "/" + part[0] + "." + ext,
            path + "/" + part[1] + "." + ext,
            v,  
            this.group,
            this,
            part[0],
            this.color);
    }

    transform = DATA.robot[this.dataIndex].transform;

    this.group.position.x = transform.pos.x;
    this.group.position.y = transform.pos.y;
    this.group.position.z = transform.pos.z;

    this.group.rotation.x = 6.28 * transform.rot.x;
    this.group.rotation.y = 6.28 * transform.rot.y;
    this.group.rotation.z = 6.28 * transform.rot.z;

    this.group.scale.x = transform.scale.x;
    this.group.scale.y = transform.scale.y;
    this.group.scale.z = transform.scale.z;
}

Robot.prototype.setPoseParameter = function(index, frac) {

    var mapping = DATA.robot[this.dataIndex].mapping;

    partSpec = mapping[index];
    for (var i = 0; i < partSpec.length; i++) {

        var part      = partSpec[i].part;
        var transform = partSpec[i].transform;
        var axis      = partSpec[i].axis;
        var min       = partSpec[i].min;
        var max       = partSpec[i].max;

        var value = calculations.lerp(frac, min, max);
        if (transform == "rotation") {
            value *= Math.PI / 180.0;
        }

        this[part][transform][axis] = value;

    }
}

Robot.prototype.setPoseTo = function(poseSpec) {
    for (var i = 0; i < poseSpec.length; i++) {
        this.setPoseParameter(i, poseSpec[i]);
    }
}

Robot.prototype.animatePoseTo = function(poseSpec, setTime, holdTime, releaseTime) {

    anim = new AnimationObject(this.neutralSpec, poseSpec, setTime, holdTime, releaseTime);
    ONFRAMEFNS.push(anim);
}

function AnimationObject(neutralSpec, poseSpec, setTime, holdTime, releaseTime) {

    this.timeToSet = setTime;
    this.timeToHold = holdTime;
    this.timeToRelease = releaseTime;

    
    this.currTime = 0;
    this.currMode = 0;

    this.poseSpec = poseSpec;
    this.neutralSpec = neutralSpec;

}

AnimationObject.prototype.update = function(t) {

    this.currTime += 1;

    switch(this.currMode) {

        case 0:
            if (this.currTime > this.timeToSet) {
                this.currMode += 1;
                this.currTime = 0;
            }
            break;

        case 1:
            if (this.currTime > this.timeToHold) {
                this.currMode += 1;
                this.currTime = 0;
            }
            break;

        case 2:
            if (this.currTime > this.timeToRelease) {
                return true;
            }
            break;
    }

    switch(this.currMode) {
        case 0:
            for (var i = 0; i < this.poseSpec.length; i++) {
                var frac = calculations.frac(this.currTime, 0, this.timeToSet);
                var giv = calculations.lerp(calculations.easeIn(frac, 2), this.neutralSpec[i], this.poseSpec[i]);
                ROBOTS[0].setPoseParameter(i, giv);
            }
            break;

        case 1:
            break;

        case 2:
            for (var i = 0; i < this.poseSpec.length; i++) {
                var frac = calculations.frac(this.currTime, 0, this.timeToRelease);
                var giv = calculations.lerp(calculations.easeOut(frac, 2), this.poseSpec[i], this.neutralSpec[i]);
                ROBOTS[0].setPoseParameter(i, giv);
            }
            break;
    }

    return false;
}