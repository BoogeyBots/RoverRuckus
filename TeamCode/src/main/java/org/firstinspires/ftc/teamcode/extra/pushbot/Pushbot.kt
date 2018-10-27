package org.firstinspires.ftc.teamcode.extra.pushbot

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.Range

@TeleOp(name = "Pushbot", group = "PUSHBOT")
class Pushbot : OpMode() {
    /** The Hardware of the robot */
    private val hardware = PushbotHardware()

    override fun init() {
        // Initialize the hardware (all the motors) using the TeleOp's hardware map
        hardware.init(hardwareMap)
    }

    override fun loop() {
        /** How much does it want to move forward - using LT */
        val throttle: Double = gamepad1.left_trigger.toDouble()
        /** How much does it want to move forward - using LT */
        val brake: Double = gamepad1.left_trigger.toDouble()

        /** How much does it want move left-right - using the right stick's X axis */
        val horizontalMovement: Double = -gamepad1.right_stick_x.toDouble()

        // Prevent overflow by clipping the values between +1 and -1
        hardware.leftMotorPower = Range.clip(throttle - brake + horizontalMovement, 1.0, -1.0)
        hardware.rightMotorPower = Range.clip(throttle - brake - horizontalMovement, 1.0, -1.0)

        // Debugging data
        telemetry.addData("Movement:", "Forward: ${throttle + brake}, Sideways: $throttle")
    }
}