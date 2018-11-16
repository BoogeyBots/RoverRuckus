package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.Range

@TeleOp(name = "ROBOT", group = "Rover Ruckus")
class RoverRuckus : OpMode() {
    private val hardware = RoverRuckusHardware()

    override fun init() {
        hardware.init(hardwareMap)
    }

    override fun loop() {
        /** How much does it want to move forward - using LT */
        val throttle: Double = gamepad1.left_trigger.toDouble()
        /** How much does it want to move forward - using LT */
        val brake: Double = gamepad1.right_trigger.toDouble()

        /** How much does it want move left-right - using the right stick's X axis */
        val horizontalMovement: Double = -gamepad1.right_stick_x.toDouble()

        // Prevent overflow by clipping the values between +1 and -1
        hardware.leftMotorPower = Range.clip(throttle - brake - horizontalMovement, -1.0, 1.0)
        hardware.rightMotorPower = Range.clip(throttle - brake + horizontalMovement, -1.0, 1.0)

        telemetry.addData("Movement:", "Throttle: $throttle | Brake: $brake | Left-Right: $horizontalMovement")
    }
}