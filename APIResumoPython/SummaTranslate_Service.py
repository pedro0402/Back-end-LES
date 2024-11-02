from flask import Flask, request, jsonify
from googletrans import Translator
from summa import summarizer
from pdfminer.high_level import extract_text
import os
from fpdf import FPDF  

app = Flask(__name__)
translator = Translator()

UPLOAD_DIR = "C:\\UploadDir"

def save_temp_file(file, filename='temp.pdf'):
    file.save(filename)
    return filename

def extract_pdf_text(file_path):
    try:
        return extract_text(file_path)
    except Exception as e:
        raise ValueError(f"Erro ao extrair texto do PDF: {str(e)}")

def generate_summary(text, ratio):
    if not text:
        raise ValueError("Nenhum texto extraído do PDF")
    
    return summarizer.summarize(text, ratio=ratio)

def translate_text(text, dest_language):
    if not text:
        raise ValueError("Nenhum texto extraído do PDF")
    
    translated_text = translator.translate(text, dest=dest_language).text
    return translated_text

def save_summary_and_translation_to_pdf(summary, translation, file_name):
    pdf = FPDF()
    pdf.add_page()
    pdf.set_font("Arial", size=12)

    pdf.cell(200, 10, txt="Resumo:", ln=True)
    pdf.multi_cell(0, 10, summary)
    
    pdf.cell(200, 10, txt="Tradução:", ln=True)
    pdf.multi_cell(0, 10, translation)

    pdf.output(os.path.join(UPLOAD_DIR, file_name))

def validate_and_extract_params(request, is_translation=False):
    if 'file' not in request.files:
        raise ValueError("Nenhum arquivo foi fornecido")
    
    file = request.files['file']
    
    if file.filename == '':
        raise ValueError("Nenhum arquivo selecionado")
    
    if not file.filename.endswith('.pdf'):
        raise ValueError("O arquivo não é um PDF")

    if is_translation:
        dest_language = request.form.get('language')
        supported_languages = {'en', 'es', 'fr', 'de', 'pt'}  
        if dest_language not in supported_languages:
            raise ValueError("Idioma de destino inválido. Idiomas suportados: en, es, fr, de, pt")
        return file, dest_language

    ratio = request.form.get('ratio', 0.2)
    try:
        ratio = float(ratio)
        if not 0 < ratio <= 1:
            raise ValueError("O ratio deve estar entre 0 e 1")
    except ValueError:
        raise ValueError("O valor do ratio deve ser um número válido entre 0 e 1")

    return file, ratio

@app.route('/summarize', methods=['POST'])
def summarize():
    try:
        file, ratio = validate_and_extract_params(request)

        temp_file_path = save_temp_file(file)

        text = extract_pdf_text(temp_file_path)

        summary = generate_summary(text, ratio)

        os.remove(temp_file_path)

        output_file_name = f"summary_{file.filename}.pdf"
        save_summary_and_translation_to_pdf(summary, "", output_file_name)

        return jsonify({'summary': summary, 'download_url': f"{UPLOAD_DIR}\\{output_file_name}"}), 200

    except ValueError as ve:
        return jsonify({'error': str(ve)}), 400
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/translate', methods=['POST'])
def translate():
    try:
        file, dest_language = validate_and_extract_params(request, is_translation=True)

        temp_file_path = save_temp_file(file)

        text = extract_pdf_text(temp_file_path)

        translated_text = translate_text(text, dest_language)

        os.remove(temp_file_path)

        output_file_name = f"translation_{file.filename}.pdf"
        save_summary_and_translation_to_pdf("", translated_text, output_file_name)

        return jsonify({'translated_text': translated_text, 'download_url': f"{UPLOAD_DIR}\\{output_file_name}"}), 200

    except ValueError as ve:
        return jsonify({'error': str(ve)}), 400
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
