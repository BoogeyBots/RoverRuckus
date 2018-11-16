package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo


class RoverRuckusHardware {
    private lateinit var leftMotor: DcMotor
    private lateinit var rightMotor: DcMotor

    private lateinit var flServo: Servo
    private lateinit var frServo: Servo

    var leftMotorPower: Double = 0.0
        // Custom setters for caching system
        set(value) {
            // Only set the power of the motor if the last "frame's" power is
            // different from the current one
            if (value != field) {
                field = value
                leftMotor.power = field
            }
        }

    var rightMotorPower: Double = 0.0
        set(value) {
            if (value != field) {
                field = value
                rightMotor.power = field
            }
        }

    var flServoPos: Double = 0.0
        set(value) {
            if (value != field) {
                field = value
                rightMotor.power = field
            }
        }

    var frServoPos: Double = 0.0
        set(value) {
            if (value != field) {
                field = value
                rightMotor.power = field
            }
        }

    fun init(hardwareMap: HardwareMap){
        leftMotor = hardwareMap.get(DcMotor::class.java, "l_motor")
        rightMotor = hardwareMap.get(DcMotor::class.java, "r_motor")

        // TODO add servo here and kill me please

        leftMotor.power = 0.0
        rightMotor.power = 0.0

        leftMotor.direction = DcMotorSimple.Direction.REVERSE
        rightMotor.direction = DcMotorSimple.Direction.FORWARD

        leftMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        rightMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }
}