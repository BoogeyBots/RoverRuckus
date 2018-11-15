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
    private lateinit var blServo: Servo
    private lateinit var brServo: Servo

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

    var blServoPos: Double = 0.0
        set(value) {
            if (value != field) {
                field = value
                blServo.position = value
            }
        }

    var brServoPos: Double = 0.0
        set(value) {
            if (value != field) {
                field = value
                brServo.position = value
            }
        }

    var flServoPos: Double = 0.0
        set(value) {
            if (value != field) {
                field = value
                flServo.position = value
            }
        }

    var frServoPos: Double  = 0.0
        set(value) {
            if (value != field) {
                field = value
                frServo.position = value
            }
        }

    fun init(hardwareMap: HardwareMap){
        leftMotor = hardwareMap.get(DcMotor::class.java, "l_motor")
        rightMotor = hardwareMap.get(DcMotor::class.java, "r_motor")
        flServo = hardwareMap.get(Servo::class.java, "fl_servo")
        frServo = hardwareMap.get(Servo::class.java, "fr_servo")
        blServo = hardwareMap.get(Servo::class.java, "bl_servo")
        brServo = hardwareMap.get(Servo::class.java, "br_servo")

        leftMotor.power = 0.0
        rightMotor.power = 0.0

        leftMotor.direction = DcMotorSimple.Direction.FORWARD
        rightMotor.direction = DcMotorSimple.Direction.REVERSE

        leftMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        rightMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }
}