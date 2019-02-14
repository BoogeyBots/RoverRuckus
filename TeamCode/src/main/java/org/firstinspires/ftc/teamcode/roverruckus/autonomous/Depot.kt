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
        robot.moveArm(1.1)
        waitForSeconds(0.5)
        robot.rotate(-170.0, 0.3)

        val goldPos = robot.recognizeGold()

        when (goldPos) {
            GoldPos.LEFT -> {
                //m50 - go towards gold
                robot.moveByCentimetersOnAngle(50.0, -150.0, 0.3)
                waitForSeconds(0.25)
                //m-50 - go back
                robot.moveByCentimetersOnAngle(-50.0, -150.0, 0.3)
                waitForSeconds(0.25)
                //r120 - rotate to fix on 90 degrees
                robot.rotate(25.0)
                //m110
                robot.moveByCentimetersOnAngle(100.0, -90.0)
                // r45
                robot.rotate(40.0, 0.3)
                // 140 spate 135
                robot.moveByCentimetersOnAngle(-160.0, -37.5)
                // drop marker
                robot.dropMarker()
                // 195 fata 135
                robot.moveByCentimetersOnAngle(220.0, -40.0)
            }
            GoldPos.MIDDLE -> {
                //m45
                robot.moveByCentimetersOnAngle(45.0, 180.0, 0.3)
                waitForSeconds(0.5)
                //m-35
                robot.moveByCentimetersOnAngle(-35.0, 180.0, 0.3)
                waitForSeconds(0.5)
                //r80
                robot.rotate(80.0, 0.3)
                //m110
                robot.moveByCentimetersOnAngle(100.0, -90.0)
                // r45
                robot.rotate(40.0, 0.3)
                // 140 spate 135
                robot.moveByCentimetersOnAngle(-160.0, -32.5)
                // drop marker
                robot.dropMarker()
                // 195 fata 135
                robot.moveByCentimetersOnAngle(220.0, -40.0)
            }
            GoldPos.RIGHT -> {
                //m50 - go towards gold
                robot.moveByCentimetersOnAngle(50.0, 135.0)
                waitForSeconds(0.25)
                //m-50 - go back
                robot.moveByCentimetersOnAngle(-50.0, 135.0)
                waitForSeconds(0.25)
                //r60 - rotate to fix on 90 degrees
                robot.rotate(-240.0, 0.3)
                //m-150 - move to wall
                robot.moveByCentimetersOnAngle(105.0, -90.0)
                //r40 - rotate to fix on 135 degrees
                robot.rotate(45.0)
                //m-150 - move to depot
                robot.moveByCentimetersOnAngle(-180.0, -32.5)
                //drop marker
                robot.dropMarker()
                //m200 - move to crater
                robot.moveByCentimetersOnAngle(250.0, -40.0)
            }
        }
        robot.parkArm()
    }

    fun initialization() {
        robot = Robot(this)
        robot.init()

        robot.lockServo.position = 0.5
        robot.hookServo.position = 0.5
        robot.markerServo.position = 0.5

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