package org.firstinspires.ftc.teamcode.extra.pushbot

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.Range

@TeleOp(name = "Pushbot", group = "PUSHBOT")
@Disabled
class Pushbot : OpMode() {
    /** The Hardware of the robot */
    private val hardware = PushbotHardware()

    private var clawOffset = 0.0
    private var stopperOffset = 0.0

    override fun init() {
        // Initialize the hardware (all the motors) using the TeleOp's hardware map
        hardware.init(hardwareMap)
    }

    override fun loop() {
        // ==================
        //      MOVEMENT
        // ==================

        /** How much does it want to move forward - using LT */
        val throttle: Double = gamepad1.left_trigger.toDouble()
        /** How much does it want to move forward - using LT */
        val brake: Double = gamepad1.right_trigger.toDouble()

        /** How much does it want move left-right - using the right stick's X axis */
        val horizontalMovement: Double = -gamepad1.right_stick_x.toDouble()

        // Prevent overflow by clipping the values between +1 and -1
        hardware.leftMotorPower = Range.clip(throttle - brake - horizontalMovement, -1.0, 1.0)
        hardware.rightMotorPower = Range.clip(throttle - brake + horizontalMovement, -1.0, 1.0)

        // ==================
        //      GRABBING
        // ==================

        // If A is pressed then move the arm down, if Y is pressed, move it up
        val armPower = when {
            gamepad2.a -> hardware.ARM_POWER
            gamepad2.y -> -hardware.ARM_POWER
            else -> 0.0
        }

        hardware.armMotorPower = armPower

        // Control claw movement using the left stick's x axis
        clawOffset += -gamepad2.left_stick_x * hardware.CLAW_SPEED

        clawOffset = Range.clip(clawOffset, -0.5, 0.5)
        hardware.leftClawServo.position = hardware.MID_SERVO + clawOffset
        hardware.rightClawServo.position = hardware.MID_SERVO - clawOffset

        stopperOffset += when {
            gamepad1.b -> 0.1
            gamepad1.x -> -0.1
            else -> 0.0
        }

        stopperOffset = Range.clip(stopperOffset, -0.5, 0.5)

        hardware.leftStopperServo.position = Range.clip(hardware.MID_SERVO + stopperOffset, 0.0, 0.5)
        hardware.rightStopperServo.position = Range.clip(hardware.MID_SERVO - stopperOffset, 0.5, 1.0)
        // Debugging data
        telemetry.addData("Stopper positions:", "Left ${hardware.leftStopperServo.position}; Right: ${hardware.rightStopperServo.position}")
        telemetry.addData("Stoppers movement:", "Left: ${Range.clip(hardware.MID_SERVO + stopperOffset, 0.0, 0.5)}; Right: ${Range.clip(hardware.MID_SERVO + stopperOffset, 0.0, 0.5)}")
        telemetry.addData("Movement:", "Forward: ${throttle - brake}, Sideways: $horizontalMovement")
        telemetry.addData("Claw servos:", "LeftPos: ${hardware.leftClawServo.position}; RightPos: ${hardware.rightClawServo.position}")
    }
}