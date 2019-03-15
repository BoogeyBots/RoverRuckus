package org.firstinspires.ftc.teamcode.roverruckus.teleop

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.roverruckus.utils.*
import org.firstinspires.ftc.teamcode.roverruckus.utils.Motors.*

@TeleOp(name = "MANCATIASOCHII", group = "Rover Ruckus")
class Controlled : OpMode() {
    val robot = Robot(this)

    val maxSpeed = 0.9
    val minSpeed = 0.3
    var currentSpeedLimit = minSpeed
    var isLiftUp: Boolean = true
    var isArmToLander: Boolean = false
    var canRotateArm: Boolean = true
    var sweeperLockStopwatch = ElapsedTime()
    val SWEEPER_LOCK_TIME = 0.2
    var sweeperLockOpen = false

    override fun init() {
        robot.init()
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        robot.telemetry = telemetry as MultipleTelemetry
    }

    override fun loop() {
        if ((robot.motors[Lift]?.mode == DcMotor.RunMode.RUN_TO_POSITION && robot.motors[Lift]?.isBusy == false) || robot.motors[Lift]?.power == 0.0) {
            robot.motors[Lift]?.power = 0.0
            robot.motors[Lift]?.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            robot.motors[Lift]?.targetPosition = 0
        }

        //======================
        //=== SPEED SETTINGS ===
        //======================
        if (gamepad1.dpad_up) {
            currentSpeedLimit = Range.clip(currentSpeedLimit + 0.01, minSpeed, maxSpeed)
        }

        if (gamepad1.dpad_down) {
            currentSpeedLimit = Range.clip(currentSpeedLimit - 0.01, minSpeed, maxSpeed)
        }
        //=======================


        //================
        //=== MOVEMENT ===
        //================
        val forwardMovement = gamepad1.left_trigger.toDouble() - gamepad1.right_trigger.toDouble()
        val strafe: Double = gamepad1.left_stick_x.toDouble()
        val rotation = gamepad1.right_stick_x

        val movLF = Range.clip(forwardMovement + rotation + strafe, -currentSpeedLimit, currentSpeedLimit)
        val movRF = Range.clip(forwardMovement - rotation - strafe, -currentSpeedLimit, currentSpeedLimit)
        val movLB = Range.clip(forwardMovement + rotation - strafe, -currentSpeedLimit, currentSpeedLimit)
        val movRB = Range.clip(forwardMovement - rotation + strafe, -currentSpeedLimit, currentSpeedLimit)
        robot.setMotorPower(LF, movLF)
        robot.setMotorPower(RF, movRF)
        robot.setMotorPower(LB, movLB)
        robot.setMotorPower(RB, movRB)
        //================

        //===============
        //=== LIFTING ===
        //===============
        if (gamepad1.x && !robot.isMotorBusy(Lift) && isLiftUp) {
            robot.setMotorMode(Lift, DcMotor.RunMode.STOP_AND_RESET_ENCODER)
            // 28 * 3.7 * ()

            robot.motors[Lift]?.targetPosition = (28 * 3.7 * -70).toInt()

            robot.motors[Lift]?.power = -0.99
            robot.motors[Lift]?.mode = DcMotor.RunMode.RUN_TO_POSITION

            isLiftUp = !isLiftUp
        }
        if (gamepad1.y && !robot.isMotorBusy(Lift) && !isLiftUp) {
            robot.setMotorMode(Lift, DcMotor.RunMode.STOP_AND_RESET_ENCODER)
            // 28 * 3.7 * ()

            robot.motors[Lift]?.targetPosition = (28 * 3.7 * 70).toInt()

            robot.motors[Lift]?.power = -0.99
            robot.motors[Lift]?.mode = DcMotor.RunMode.RUN_TO_POSITION

            isLiftUp = !isLiftUp
        }
        //===============

        //===========================
        //=== INTAKE ARM ROTATION ===
        //===========================
        val intakeArmPower = gamepad2.right_stick_y.toDouble()
        robot.motors[IntakeRotation]?.power = Range.clip(intakeArmPower, -0.65, 0.65)

        //==========================
        //=== INTAKE ARM LUNGIRE ===
        //==========================
        val armPower = -gamepad2.left_stick_y.toDouble()
        robot.motors[IntakeExtension]?.power = armPower.clip(-0.8, 0.8)

        //===============
        //=== SWEEPER ===
        //===============
        val sweeperPower = gamepad2.left_bumper.toDouble() - gamepad2.right_bumper.toDouble()
        robot.motors[Sweeper]?.power = sweeperPower.clip(-0.75, 0.75)

        //====================
        //=== SWEEPER LOCK ===
        //====================
        if (sweeperLockStopwatch.seconds() >= SWEEPER_LOCK_TIME) {
            if (gamepad1.b) {
                sweeperLockStopwatch.reset()
                if (sweeperLockOpen) {
                    robot.servos[Servos.SweeperLock]?.position = 1.0
                } else {
                    robot.servos[Servos.SweeperLock]?.position = 0.65
                }

                sweeperLockOpen = !sweeperLockOpen
            }
        }

        robot.writeMotorsTelemetry()
        robot.writeServosTelemetry()
    }
}