package com.timposu.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UploadController {
	
	private static String UPLOAD_DIR = "/img";
	
	@GetMapping("/")
	public String index() {
		return "upload";
	}
	
	@PostMapping("/upload")
	public String singleUploadFile(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes, HttpSession session) {
		
		ServletContext context = session.getServletContext();
		
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select the file");
			return "redirect:uploadStatus";
		}
		
		try {
			String pathimg = context.getRealPath(UPLOAD_DIR);
			
			byte[] bytes = file.getBytes();
			Path path = Paths.get(pathimg + File.separator + file.getOriginalFilename());
			Files.write(path, bytes);
			
			System.out.println(path);
			
			redirectAttributes.addFlashAttribute("message", "Uploaded successfully " + 
					file.getOriginalFilename());
			redirectAttributes.addFlashAttribute("filename", file.getOriginalFilename());			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "redirect:uploadStatus";
	}
	
	@GetMapping("/uploadStatus")
	public String uploadStatus() {
		return "uploadStatus";
	}
	
	// Multi file
	// ==========
	
	@GetMapping("/multi")
	public String uploadMultiPage() {
		return "uploadMulti";
	}
	
	@PostMapping("/uploadMulti")
	public String uploadMulti(@RequestParam("files") MultipartFile[] files,
			RedirectAttributes redirectAttributes, HttpSession session) {
		
		ServletContext context = session.getServletContext();
		String pathimg = context.getRealPath(UPLOAD_DIR);
		StringJoiner joiner = new StringJoiner(" , ");
		
		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				continue;
			}
			
			try {
				byte[] bytes = file.getBytes();
				Path path = Paths.get(pathimg + File.separator + file.getOriginalFilename());
				Files.write(path, bytes);
				
				joiner.add(file.getOriginalFilename());
				
				System.out.println(path);		
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String uploadedFileName = joiner.toString();
		if (StringUtils.isEmpty(uploadedFileName)) {
			redirectAttributes.addFlashAttribute("message", "Please select the file");
		} else {
			redirectAttributes.addFlashAttribute("message", "Uploaded successfully " + 
					uploadedFileName);
		}
		
		return "redirect:uploadStatus";
	}

}
