package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot
import org.firstinspires.ftc.teamcode.roverruckus.utils.toInches
import org.firstinspires.ftc.teamcode.roverruckus.utils.waitForSeconds

@Autonomous(name = "Crater", group = "Rover Ruckus")
class Crater : LinearOpMode() {
    lateinit var robot: Robot

    override fun runOpMode() {
        initialization()
        waitForStart()

        robot.liftLock()
        robot.dropDown()
        robot.pushLander()
        waitForSeconds(0.25)
        robot.detachHook()
        robot.moveArm()
        robot.rotate(-170.0, 0.3)

        val goldPos = robot.recognizeGold()

        when (goldPos) {
            GoldPos.LEFT -> {
                //m50 - go towards gold
                robot.moveByCentimetersOnAngle(50.0, -136.0, 0.3)
                waitForSeconds(0.25)
                //m-50 - go back
                robot.moveByCentimetersOnAngle(-50.0, -136.0, 0.3)
                waitForSeconds(0.25)
                //r120 - rotate to fix on 90 degrees
                robot.rotate(-110.0)
                //m-100 - move to wall
                robot.moveByCentimetersOnAngle(-150.0, 90.0)
                //r40 - rotate to fix on 135 degrees
                robot.rotate(40.0)
                //m-150 - move to depot
                robot.moveByCentimetersOnAngle(-80.0, 142.0)
                robot.moveByCentimetersOnAngle(-80.0, 148.0)

                // daca te-ai strambat mai mult de 20 de grade
                if (robot.angles.firstAngle > 150.0) {
                    robot.rotateTo(135.0)
                }
                // fa-te la loc

                //drop marker
                robot.dropMarker()
                //m200 - move to crater
                robot.moveByCentimetersOnAngle(115.0, 140.0)
                robot.moveByCentimetersOnAngle(115.0, 146.0)
            }
            GoldPos.MIDDLE -> {
                robot.moveByCentimetersOnAngle(45.0, 180.0, 0.3)
                waitForSeconds(0.5)
                robot.moveByCentimetersOnAngle(-35.0, 180.0, 0.3)
                waitForSeconds(0.5)
                robot.rotate(-80.0, 0.3)
                // 120 spate 90
                robot.moveByCentimetersOnAngle(-135.0, 90.0)
                // rotit 45
                robot.rotate(40.0, 0.3)
                // 140 spate 135
                robot.moveByCentimetersOnAngle(-150.0, 145.0)
                // drop marker
                robot.dropMarker()
                // 195 fata 135
                robot.moveByCentimetersOnAngle(105.0, 142.0)
                robot.moveByCentimetersOnAngle(105.0, 145.5)
            }
            GoldPos.RIGHT -> {
                //m50 - go towards gold
                robot.moveByCentimetersOnAngle(50.0, 135.0)
                waitForSeconds(0.25)
                //m-50 - go back
                robot.moveByCentimetersOnAngle(-50.0, 135.0)
                waitForSeconds(0.25)
                //r60 - rotate to fix on 90 degrees
                robot.rotate(-40.0, 0.3)
                //m-150 - move to wall
                robot.moveByCentimetersOnAngle(-145.0, 90.0)
                //r40 - rotate to fix on 135 degrees
                robot.rotate(40.0)
                //m-150 - move to depot
                robot.moveByCentimetersOnAngle(-180.0, 142.0)
                //drop marker
                robot.dropMarker()
                //m200 - move to crater
                robot.moveByCentimetersOnAngle(230.0, 143.0)
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