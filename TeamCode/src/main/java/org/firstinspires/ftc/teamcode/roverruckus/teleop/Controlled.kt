package org.firstinspires.ftc.teamcode.roverruckus.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.roverruckus.autonomous.writeMotorsTelemetry
import org.firstinspires.ftc.teamcode.roverruckus.utils.Motors.*
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot

@TeleOp(name = "MANCATIASOCHII", group = "Rover Ruckus")
class Controlled : OpMode() {
    val robot = Robot(this)

    val maxSpeed = 0.9
    val minSpeed = 0.3
    var currentSpeedLimit = minSpeed
    var isLiftUp: Boolean = false
    var isArmToLander: Boolean = false
    var canRotateArm: Boolean = true

    override fun init() {
        robot.init()
    }

    override fun loop() {
        if ((robot.motors[Lift]?.mode == DcMotor.RunMode.RUN_TO_POSITION && robot.motors[Lift]?.isBusy == false) || robot.motors[Lift]?.power == 0.0) {
            robot.motors[Lift]?.power = 0.0
            robot.motors[Lift]?.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            robot.motors[Lift]?.targetPosition = 0
        }

        if (!robot.isMotorBusy(IntakeArm) && !canRotateArm) {
            robot.setMotorPower(IntakeArm, 0.0)
            robot.setMotorMode(IntakeArm, DcMotor.RunMode.RUN_WITHOUT_ENCODER)
            canRotateArm = true
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
        val forwardMovement = -gamepad1.left_stick_y.toDouble()
        val strafe: Double = -gamepad1.left_stick_x.toDouble()
        val rotation = gamepad1.right_stick_x.toDouble()

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
        if (gamepad1.a && !robot.isMotorBusy(Lift)) {
            robot.setMotorMode(Lift, DcMotor.RunMode.STOP_AND_RESET_ENCODER)
            // 28 * 3.7 * ()

            robot.motors[Lift]?.targetPosition = (28 * 3.7 * if (isLiftUp) -70 else 70).toInt()

            robot.motors[Lift]?.power = -0.99
            robot.motors[Lift]?.mode = DcMotor.RunMode.RUN_TO_POSITION

            isLiftUp = !isLiftUp
        }
        //===============

        //===========================
        //=== INTAKE ARM ROTATION ===
        //===========================
        if (gamepad2.a && !robot.isMotorBusy(IntakeArm)) {
            robot.setMotorMode(IntakeArm, DcMotor.RunMode.STOP_AND_RESET_ENCODER)

            // 312 * CPR * rotations
            val targetPos = (312 * 28 * 0.2) * (if (isArmToLander) 1 else -1)
            robot.setMotorTargetPos(IntakeArm, targetPos)
            robot.setMotorPower(IntakeArm, -0.2)
            robot.setMotorsMode(DcMotor.RunMode.RUN_TO_POSITION, IntakeArm)

            isArmToLander = !isArmToLander
            canRotateArm = false
        }
        if (canRotateArm && robot.motors[IntakeArm]?.mode == DcMotor.RunMode.RUN_WITHOUT_ENCODER) {
            val intakeArmPower = -gamepad2.right_stick_y.toDouble()
            robot.motors[IntakeArm]?.power = Range.clip(intakeArmPower, -0.5, 0.5)
        }

        telemetry.addData("FMM", "$canRotateArm")
        robot.writeMotorsTelemetry()
    }
}