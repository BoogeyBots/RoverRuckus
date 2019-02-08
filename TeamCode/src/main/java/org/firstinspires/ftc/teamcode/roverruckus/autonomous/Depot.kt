package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot
import org.firstinspires.ftc.teamcode.roverruckus.utils.waitForSeconds

@Autonomous(name = "Depot", group = "Rover Ruckus")
class Depot : LinearOpMode() {
    lateinit var robot: Robot

    override fun runOpMode() {
        initialization()
        waitForStart()

        robot.liftLock()
        robot.dropDown()
        robot.pushLander()
        robot.detachHook()
        robot.rotate(-170.0)
    }

    fun initialization() {
        robot = Robot(this)
        robot.init()

        robot.lockServo.position = 0.5
        robot.hookServo.position = 0.5
        robot.markerServo.position = 0.0

        robot.initVuforia()

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            robot.initTfod()
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD")
        }

        robot.composeTelemetry()
    }

    override fun waitForStart() {
        // === WAIT FOR START ===
        while (!opModeIsActive() && !isStopRequested) {
            telemetry.addData("Status", "Waiting in Init")
            telemetry.update()
        }
        // ======================
    }
}