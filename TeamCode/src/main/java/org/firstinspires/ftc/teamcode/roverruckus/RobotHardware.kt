package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo


class RobotHardware {
    private lateinit var leftMotor: DcMotor
    private lateinit var rightMotor: DcMotor

    lateinit var leftServo: Servo
    lateinit var rightServo: Servo
    lateinit var leftServo2: Servo
    lateinit var rightServo2: Servo

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

    fun init(hardwareMap: HardwareMap){
        leftMotor=hardwareMap.get(DcMotor:: class.java, "left_motor")
        rightMotor = hardwareMap.get(DcMotor::class.java, "right_motor")
        leftServo = hardwareMap.get(Servo::class.java, "left_servo")
        leftServo2 = hardwareMap.get(Servo::class.java, "left_servo_2")
        rightServo = hardwareMap.get(Servo::class.java, "right_servo")
        rightServo2 = hardwareMap.get(Servo::class.java, "right_servo_2")

        leftMotor.power = 0.0
        rightMotor.power = 0.0

        leftMotor.direction = DcMotorSimple.Direction.FORWARD
        rightMotor.direction = DcMotorSimple.Direction.REVERSE

        leftMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        rightMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER


    }

}