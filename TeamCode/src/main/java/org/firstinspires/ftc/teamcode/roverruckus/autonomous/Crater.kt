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

//        robot.liftLock()
//        robot.dropDown()
//        robot.pushLander()
//        robot.detachHook()
//        robot.rotate(-170.0, 0.3)

        robot.moveByCentimetersOnAngle(35.0, 0.0, 0.3)

        val goldPos = robot.recognizeGold()

        when (goldPos) {
            "LEFT" -> {
                robot.moveByCentimetersOnAngle(65.0, 40.0, 0.3)
                waitForSeconds(0.5)
                robot.moveByCentimetersOnAngle(-70.0, 40.0, 0.3)
                waitForSeconds(0.5)
                robot.rotate(-110.0)
            }
            "CENTER" -> {
                robot.moveByCentimetersOnAngle(45.0, 0.0, 0.3)
                waitForSeconds(0.5)
                robot.moveByCentimetersOnAngle(-40.0, 0.0, 0.3)
                waitForSeconds(0.5)
                robot.rotate(-80.0, 0.3)
            }
            "RIGHT" -> {

            }
        }

        robot.moveByCentimetersOnAngle(-155.0, -90.0)
        robot.rotate(40.0, 0.3)
        robot.moveByCentimetersOnAngle(-140.0, -45.0)
        waitForSeconds(0.5)
        robot.moveByCentimetersOnAngle(260.0, -45.0)
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