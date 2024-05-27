package com.example.demo_webdav

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.demo_webdav.databinding.ActivityMainBinding
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置创建文件夹按钮的点击事件
        binding.create.setOnClickListener {
            // 启动子线程
            thread {
                // 连接到坚果云WebDAV服务器，并返回sardine对象
                val sardine = initSardine()
                // 文件夹不存在则创建一个
                if (!checkExistence(sardine)) {
                    createDir(sardine)
                }
            }
        }

        // 设置上传文件按钮的点击事件
        binding.upload.setOnClickListener {
            // 启动子线程
            thread {
                val sardine = initSardine()
                uploadFile(sardine, "demo_webdav的测试文本，这将生成为一个txt文本存在WebDAV服务器端")
            }
            // Toast一下提醒已上传
            Toast.makeText(this, "已上传", Toast.LENGTH_SHORT).show()
        }

        // 设置下载文件按钮的点击事件
        binding.download.setOnClickListener {
            var fileContent = ""
            // 启动子线程
            thread {
                val sardine = initSardine()
                fileContent = downloadFile(sardine)
            }.join()  // 阻塞子线程，以取得fileContent的值
            // 将fileContent的值赋予show文本控件显示出来
            binding.show.text = fileContent
        }

        // 设置重命名文件夹按钮的点击事件
        binding.rename.setOnClickListener {
            // 启动子线程
            thread {
                val sardine = initSardine()
                moveOrRenameFile(sardine)
            }
            // Toast一下提醒已重命名
            Toast.makeText(this, "已重命名", Toast.LENGTH_SHORT).show()
        }

        // 设置删除文件夹按钮的点击事件
        binding.delete.setOnClickListener {
            // 启动子线程
            thread {
                val sardine = initSardine()
                deleteFile(sardine)
            }
            // Toast一下提醒已删除
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
        }

        //切换动画
        binding.animation.setOnClickListener{
            val intent = Intent(this, AnimationActivity::class.java)
            startActivity(intent)
        }
    }

    // 与坚果云WebDAV服务器建立连接，返回sardine对象以进行操作
    private fun initSardine(): OkHttpSardine {
        val sardine = OkHttpSardine()
        // 坚果云的账号
        val userName = "fangshancun@gmail.com"
        // 授权给第三方应用的密码口令
        val passWord = "awdw3cwkrmmrey3y"
        // 建立连接
        sardine.setCredentials(userName, passWord)
        // 返回sardine对象
        return sardine
    }

    private fun createDir(sardine: OkHttpSardine) {
        val dirPath = "https://dav.jianguoyun.com/dav/demo_webdav文件夹"
        sardine.createDirectory(dirPath)
    }

    private fun checkExistence(sardine: OkHttpSardine): Boolean {
        val dirPath = "https://dav.jianguoyun.com/dav/demo_webdav文件夹"
        return sardine.exists(dirPath)
    }

    private fun uploadFile(sardine: OkHttpSardine, fileContent: String) {
        val filePath = "https://dav.jianguoyun.com/dav/demo_webdav文件夹/测试文本.txt"
        // 将变量转变为byte字节数组，以传输到网盘
        val data = fileContent.toByteArray()
        sardine.put(filePath, data)
    }

    private fun downloadFile(sardine: OkHttpSardine): String {
        val filePath = "https://dav.jianguoyun.com/dav/demo_webdav文件夹/测试文本.txt"
        val download = sardine.get(filePath)
        // 以文件流的形式读取下载的文件，并转换为字符串
        val fileContent = BufferedReader(InputStreamReader(download)).useLines { lines ->
            val results = StringBuilder()
            lines.forEach {
                results.append(it)
            }
            results.toString()
        }
        return fileContent
    }

    private fun moveOrRenameFile(sardine: OkHttpSardine) {
        val oldPath = "https://dav.jianguoyun.com/dav/demo_webdav文件夹"
        val newPath = "https://dav.jianguoyun.com/dav/renamed_or_moved_demo_webdav文件夹"
        sardine.move(oldPath, newPath)
    }

    private fun deleteFile(sardine: OkHttpSardine) {
        val filePath = "https://dav.jianguoyun.com/dav/renamed_or_moved_demo_webdav文件夹"
        sardine.delete(filePath)
    }
}


