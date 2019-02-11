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
                robot.moveByCentimetersOnAngle(-150.0, 142.5)
                // 195 fata 135
                robot.moveByCentimetersOnAngle(210.0, 140.0)
            }
            GoldPos.RIGHT -> {

            }
        }

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